/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.print;

import ch.ethz.topobench.Main;
import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.Link;
import ch.ethz.topobench.graph.patheval.PathEvaluator;
import ch.ethz.topobench.graph.traffic.TrafficPair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Vector;

public class PrinterMCFFairCondensed {

    private final int numNodes;
    private final Graph graph;
    private final int switchLevelMatrix[][];
    private final Vector<Link> adjacencyList[];
    private final PathEvaluator pathEvaluator;

    public PrinterMCFFairCondensed(Graph graph, PathEvaluator pathEvaluator) {
        this.graph = graph;
        this.numNodes = graph.getNumNodes();
        this.switchLevelMatrix = new int[numNodes][numNodes];
        this.adjacencyList = graph.getAdjacencyList();
        this.pathEvaluator = pathEvaluator;
    }

    private class FlowID {

        int flowID;
        int srcSwitch;
        int dstSwitch;

        FlowID(int fid, int s, int d) {
            flowID = fid;
            srcSwitch = s;
            dstSwitch = d;
        }

    }

    // If slack is set to 3, flows can not deviate more than 2 hops from their shortest path distance
    private boolean isFlowZero(FlowID flowID, int linkFrom, int linkTo) {
        return pathEvaluator.isFlowZero(flowID.srcSwitch, flowID.dstSwitch, linkFrom, linkTo);
    }

    /**
     * Print the MCF fair condensed linear program for the given graph and server-level traffic matrix.
     *
     * @param traffic       Traffic matrix
     */
    public void print(List<TrafficPair> traffic) {

        // Calculate shortest paths; important for the slack path evaluator
        graph.calculateShortestPaths();

        try {

            // Open stream
            FileWriter fileStream = new FileWriter("temp/lp/program.lp");
            BufferedWriter out = new BufferedWriter(fileStream);

            // Put the switch traffic pairs (representing server level matrix)
            // into the switch level traffic matrix
            for (TrafficPair pair : traffic) {
                int from = pair.getFrom();
                int to = pair.getTo();

                int fromsw = graph.svrToSwitch(from);
                int tosw = graph.svrToSwitch(to);

                // Flow going to the same switch is not allowed
                if (fromsw == tosw) {
                    throw new IllegalArgumentException("Illegal traffic; impossible to have traffic between servers on the same switch.");
                }

                // Add entry to the traffic matrix
                switchLevelMatrix[fromsw][tosw]++;
            }

            // Calculate number of flows existing in the switch-level matrix
            int numFlows = 0;
            for (int f = 0; f < numNodes; f++) {
                for (int t = 0; t < numNodes; t++) {
                    if (switchLevelMatrix[f][t] > 0) {
                        numFlows++;
                    }
                }
            }

            // Map each flow identifier to its corresponding source-destination pair (f, t)
            FlowID[] allFlowIDs = new FlowID[numFlows];
            Writer outputFlowIdMap = new BufferedWriter(new FileWriter("temp/lp/flow_id_map"));
            int currentID = 0;
            for (int f = 0; f < numNodes; f++) {
                for (int t = 0; t < numNodes; t++) {
                    if (switchLevelMatrix[f][t] > 0) {
                        allFlowIDs[currentID] = new FlowID(currentID, f, t);
                        outputFlowIdMap.write(currentID + " " + f + " " + t + "\n");
                        currentID++;
                    }
                }
            }
            outputFlowIdMap.close();

            // For every link (u, v) with capacity cap print link capacities
            Writer outputLinkCaps = new BufferedWriter(new FileWriter("temp/lp/link_caps"));
            for (int u = 0; u < numNodes; u++) {
                for (Link link : adjacencyList[u]) {
                    int cap = link.getLinkCapacity();
                    int v = link.getLinkTo();
                    outputLinkCaps.write(u + "-" + v + " (" + cap + ") " + adjacencyList[u].size() + " " + adjacencyList[v].size() + "\n");
                }
            }
            outputLinkCaps.close();

            ///////////////////////////
            // OBJECTIVE
            //

            out.write("Maximize \n");
            out.write("obj: K\n");
            out.write("\nSUBJECT TO\n");

            ///////////////////////////
            // TYPE 0: flow fairness;
            // all flow allocated for the f->t must exit f
            //

            out.write("\\Type 0: Outgoing flow >= K\n");

            // For each flow f -> t with flow id fid
            int fid = 0;
            for (int f = 0; f < numNodes; f++) {
                for (int t = 0; t < numNodes; t++) {
                    if (switchLevelMatrix[f][t] > 0)      //for each flow fid with source f
                    {

                        // Constraint identifier
                        String constraint = "c0_" + fid + ": ";

                        // Add to the constraint all links from f that are allowed to carry the flow
                        int writeCons = 0;
                        for (int j = 0; j < adjacencyList[f].size(); j++)   // For each out link of f = (f,j)
                        {
                            if (!isFlowZero(allFlowIDs[fid], f, adjacencyList[f].elementAt(j).getLinkTo())) {
                                constraint += "-f_" + fid + "_" + f + "_" + adjacencyList[f].elementAt(j).getLinkTo() + " ";
                                writeCons = 1;
                            }
                        }

                        // Only write the constraint
                        if (writeCons == 1) {
                            constraint += " + " + switchLevelMatrix[f][t] + " K <= 0\n";
                            out.write(constraint);
                        }

                        fid++;
                    }
                }
            }

            ///////////////////////////
            // TYPE 1: link capacity;
            // total summed flow cannot exceed capacity
            //

            out.write("\n\\Type 1: Load on link <= link capacity\n");

            // For every link (i, j)
            for (int linkFrom = 0; linkFrom < numNodes; linkFrom++) {
                for (int j = 0; j < adjacencyList[linkFrom].size(); j++) {

                    // Local scope link information
                    Link link = adjacencyList[linkFrom].elementAt(j);
                    int linkTo = link.getLinkTo();
                    int linkCapacity = link.getLinkCapacity();

                    StringBuilder curConstraint = new StringBuilder();
                    boolean writeConstraint = false;

                    // For every flow
                    for (int fid1 = 0; fid1 < numFlows; fid1++) {

                        // Check if the flow is allowed to go over the link
                        if (!isFlowZero(allFlowIDs[fid1], linkFrom, linkTo)) {
                            if (writeConstraint) {
                                curConstraint.append(" + ");
                            }
                            curConstraint.append("f_" + fid1 + "_" + linkFrom + "_" + linkTo);
                            writeConstraint = true;
                        }

                    }

                    // Only write constraint
                    String constraint = curConstraint.toString();
                    if (writeConstraint) {
                        out.write("c1_" + linkFrom + "_" + linkTo + ": " + constraint + " <= " + linkCapacity + "\n");
                    }

                }
            }

            ///////////////////////////
            // TYPE 2: Flow conservation at intermediary nodes
            out.write("\n\\Type 2: Flow conservation at non-source, non-destination\n");

            // For each flow f -> t with flow id fid
            fid = 0;
            for (int f = 0; f < numNodes; f++) {
                for (int t = 0; t < numNodes; t++) {
                    if (switchLevelMatrix[f][t] > 0)
                    {

                        // For each node in the graph
                        for (int u = 0; u < numNodes; u++)
                        {

                            // Flow conservation constraints for source nodes
                            if (u == f)
                            {

                                // Declare constraint variables
                                boolean writeConstraintMaxOutFlow = false;
                                boolean writeConstraintNoSelfInFlow = false;
                                String constraintMaxOutFlow = "c2_" + fid + "_" + u + "_1: ";
                                String constraintNoSelfInFlow = "c2_" + fid + "_" + u + "_2: ";

                                // For each out link of u (= f): (u,v)
                                for (int j = 0; j < adjacencyList[u].size(); j++)   // For each out-link of u: (u,v)
                                {
                                    int v = adjacencyList[u].elementAt(j).getLinkTo();

                                    // Add outgoing links (u, v) to the max out-flow constraint
                                    if (!isFlowZero(allFlowIDs[fid], u, v)) {
                                        if (writeConstraintMaxOutFlow) {
                                            constraintMaxOutFlow += " + ";
                                        }
                                        constraintMaxOutFlow += "f_" + fid + "_" + u + "_" + v;
                                        writeConstraintMaxOutFlow = true;
                                    }

                                    // Add incoming links (v, u) to the no self-in-flow constraint
                                    if (!isFlowZero(allFlowIDs[fid], v, u)) {
                                        if (writeConstraintNoSelfInFlow) {
                                            constraintNoSelfInFlow += " + ";
                                        }
                                        constraintNoSelfInFlow += "f_" + fid + "_" + v + "_" + u;
                                        writeConstraintNoSelfInFlow = true;
                                    }

                                }

                                // Write max out-flow constraint
                                if (writeConstraintMaxOutFlow) {
                                    out.write(constraintMaxOutFlow + " <= " + switchLevelMatrix[f][t] * Main.K_UPPER_BOUND + "\n");
                                }

                                // Write no self-in-flow constraint
                                if (writeConstraintNoSelfInFlow) {
                                    out.write(constraintNoSelfInFlow + " = 0\n");
                                }

                            }
                            else if (u != t)  // Flow conservation constraints for intermediary (non-src, non-dst) nodes
                            {

                                String constraint = "c2_" + fid + "_" + u + "_3: ";
                                boolean writeCons = false;

                                // Sum up all flow with id fid going out of u
                                for (int j = 0; j < adjacencyList[u].size(); j++)   // For each out-link of u = (u,v)
                                {
                                    int v = adjacencyList[u].elementAt(j).getLinkTo();
                                    if (!isFlowZero(allFlowIDs[fid], u, v)) {
                                        if (writeCons) {
                                            constraint += " + ";
                                        }
                                        constraint += "f_" + fid + "_" + u + "_" + v;
                                        writeCons = true;
                                    }
                                }

                                // Negative sum up all the flow with id fid going into u
                                for (int j = 0; j < adjacencyList[u].size(); j++)   // For each in link-of u = (v,u)
                                {
                                    int v = adjacencyList[u].elementAt(j).getLinkTo();
                                    if (!isFlowZero(allFlowIDs[fid], v, u)) {
                                        constraint += " - f_" + fid + "_" + v + "_" + u;
                                        writeCons = true;
                                    }
                                }

                                // Finally write intermediary flow conservation constraint
                                if (writeCons) {
                                    out.write(constraint + " = 0\n");
                                }

                            }
                        }

                        // Onto next flow
                        fid++;

                    }
                }

            }

            // End of linear program
            out.write("End\n");

            // Close stream
            out.close();

        } catch (IOException e) {
            System.err.println("PrinterMCFFairCondensed: print: " + e.getMessage());
        }

    }

}
