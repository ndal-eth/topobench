/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.print;

import ch.ethz.topobench.graph.Link;
import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.traffic.TrafficPair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.Vector;

public class PrinterSimple {

    private final int numNodes;
    private final Graph graph;
    private final int switchLevelMatrix[][];
    private final Vector<Link> adjacencyList[];

    public PrinterSimple(Graph graph) {
        this.graph = graph;
        numNodes = graph.getNumNodes();
        switchLevelMatrix = new int[numNodes][numNodes];
        adjacencyList = graph.getAdjacencyList();
    }

    /**
     * Print the simple linear program for all-to-all cases.
     *
     * @param traffic       Traffic pairs
     */
    public void print(List<TrafficPair> traffic) {

        try {

            // Open output stream
            FileWriter fileStream = new FileWriter("temp/program.lp");
            BufferedWriter out = new BufferedWriter(fileStream);

            // Shortly print the number of edges and traffic pairs
            int edgeID = 0;
            for (int i = 0; i < numNodes; i++) {
                for (int j = 0; j < adjacencyList[i].size(); j++) {
                    edgeID++;
                }
            }
            System.out.print(" " + edgeID + " edges, " + traffic.size() + " pairs...");

            // Determine how many times pairs occur
            for (TrafficPair pair : traffic) {
                int fromsw = graph.svrToSwitch(pair.getFrom());
                int tosw = graph.svrToSwitch(pair.getTo());

                // Links to inner-switch servers are not allowed
                if (fromsw == tosw) {
                    throw new IllegalArgumentException("Illegal traffic; impossible to have traffic between servers on the same switch.");
                }

                // Increase how many servers want to get from a to b
                switchLevelMatrix[fromsw][tosw]++;

            }

            // Write objective K * (Traffic Matrix), maximizing K
            out.write("Maximize \n");
            out.write("obj: ");
            String objective = "K";
            out.write(objective);

            // Type 0 - Flows >= K
            out.write("\n\nSUBJECT TO \n\\Type 0: Flow >= K\n");

            // For every traffic pair of nodes (a, b), the sum of all flows can only be maximum K * n_{traffic_pairs}
            int fid = 0;
            for (int f = 0; f < numNodes; f++) {
                for (int t = 0; t < numNodes; t++) {
                    if (switchLevelMatrix[f][t] > 0) {
                        String constraint = "c0_" + fid + ": ";
                        constraint += "- f_" + f + "_" + t + " ";
                        constraint += " + " + switchLevelMatrix[f][t] + " K <= 0\n";
                        out.write(constraint);
                        fid++;
                    }
                }
            }

            // TYPE 1: sum of all flows going through link is less than or equal to link capacity
            out.write("\n\\Type 1: Link capacity constraint\n");

            // For every edge (i, z)
            for (int i = 0; i < numNodes; i++) {
                for (int j = 0; j < adjacencyList[i].size(); j++) {

                    // Retrieve link...
                    Link link = adjacencyList[i].elementAt(j);
                    int z = link.getLinkTo();
                    int capacity = link.getLinkCapacity();

                    // Constraint name
                    String constraint = "c2_" + i + "_" + z + ": ";

                    // The sum of flow from all nodes going through the edge
                    for (int k = 0; k < numNodes; k++) {
                        constraint += " + l_" + i + "_" + z + "_" + k;
                    }

                    // Is less than or equal to the capacity of the link
                    constraint += " <= " + capacity + "\n";

                    out.write(constraint);

                }
            }

            // Type 2 - flow conservation at nodes
            out.write("\n\\Type 2: Flow conservation at node\n");

            for (int i = 0; i < numNodes; i++) {
                for (int k = 0; k < numNodes; k++) {

                    // For every node pair not to itself
                    if (i != k) {
                        String constraint = "c3_" + i + "_" + k + ": ";

                        // The amount of flow from i to k
                        if (switchLevelMatrix[i][k] > 0) {
                            constraint += " f_" + i + "_" + k;
                        }

                        // Plus, how much flows goes over edge (j, i) towards k
                        for (int j = 0; j < adjacencyList[i].size(); j++) {
                            constraint += " + l_" + adjacencyList[i].elementAt(j).getLinkTo() + "_" + i + "_" + k + " ";
                        }

                        // Minus, how much flows over edge (i, j) towards k
                        for (int j = 0; j < adjacencyList[i].size(); j++) {
                            constraint += " - l_" + i + "_" + adjacencyList[i].elementAt(j).getLinkTo() + "_" + k + " ";
                        }

                        // Should be equal to 0
                        constraint += " = 0\n";
                        out.write(constraint);

                    }
                    else // i == k, for every node pair which is itself
                    {

                        String constraint = "c3_" + i + "_" + k + ": ";

                        // Minus the total flow that is destined to go into i
                        for (int j = 0; j < numNodes; j++) {
                            if (switchLevelMatrix[j][i] > 0) {
                                constraint += " - f_" + j + "_" + i;
                            }
                        }

                        // Plus, the sum of all flow actually coming from edges (j, i) into i
                        for (int j = 0; j < adjacencyList[i].size(); j++) {
                            constraint += " + l_" + adjacencyList[i].elementAt(j).getLinkTo() + "_" + i + "_" + i + " ";
                        }

                        // Is equal to 0
                        constraint += " = 0\n";
                        out.write(constraint);

                    }
                }
            }

            out.close();

        } catch (Exception e) {
            System.err.println("PrinterSimple Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
