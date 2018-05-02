/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.traffic;

import ch.ethz.topobench.Main;
import ch.ethz.topobench.graph.Graph;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Create a truncated variant of max-weight traffic pairs.
 *
 * Procedure:
 *
 * (1) Calculate max weight traffic pairs among nodes.
 * (2) Go from top to bottom until x% of nodes is selected.
 * (3) Do a max-weight matching between the selected x% nodes
 * (4) For each matched node pair, create pair for one server of the left to sent to one distinct server of the right
 * (5) Return the server-level pairs from step 4.
  */
public class MaxWeightPairsTraffic extends Traffic {

    private final boolean isMaxMatching;
    private final double fraction;

    public MaxWeightPairsTraffic(Graph graph, boolean isMaxMatching, double fraction) {
        super(graph);
        this.isMaxMatching = isMaxMatching;
        this.fraction = fraction;
        ensureNodesWithWeightAreUniform();
    }

    @Override
    public List<TrafficPair> generate() {

        // Log start
        System.out.println(" > Generating Weight-Matching traffic pairs...");

        // Calculate shortest paths
        graph.calculateShortestPaths();

        // Do a full maximum weight pair matching among all weighed nodes
        printMaxWeightProblem("temp/weighed_node_distances.txt", false, null);
        ArrayList<TrafficPair> allPairs = trafficWeightPairsUsingDistanceFile("temp/weighed_node_distances.txt");

        // Take the top fraction of nodes in the ordered maximum weight pairs
        boolean[] inFraction = new boolean[numNodesWithWeight];
        int fractionSize = (int) (numNodesWithWeight * fraction);

        int added = 0;
        for (TrafficPair tp : allPairs) {

            int from = tp.getFrom();
            int to = tp.getTo();

            if (added >= fractionSize) {
                break;
            }

            if (!inFraction[from]) {
                inFraction[from] = true;
                added++;
            }

            if (added >= fractionSize) {
                break;
            }

            if (!inFraction[to]) {
                inFraction[to] = true;
                added++;
            }

        }

        // Do a maximum weight pair matching among only the weighed nodes in the fraction
        printMaxWeightProblem("temp/weighed_node_distances_mw_fraction.txt", true, inFraction);
        ArrayList<TrafficPair> fractionPairs = trafficWeightPairsUsingDistanceFile("temp/weighed_node_distances_mw_fraction.txt");

        ArrayList<TrafficPair> ls = new ArrayList<>();
        for (TrafficPair p : fractionPairs) {

            // Retrieve original switch identifiers
            int fromSwitch = nodesWithWeight.get(p.getFrom());
            int toSwitch = nodesWithWeight.get(p.getTo());

            // Retrieve all servers on both switches
            List<Integer> fromSvrs = graph.switchToSvrs(fromSwitch);
            List<Integer> toSvrs = graph.switchToSvrs(toSwitch);

            // Size of fromSvrs and toSvrs must be equal due to the call
            // in the constructor: ensureNodesWithWeightAreUniform

            // Couple one of the servers of one switch to one distinct server of the other switch
            for (int z = 0; z < fromSvrs.size(); z++) {
                ls.add(new TrafficPair(fromSvrs.get(z), toSvrs.get(z)));
            }

        }

        // Print log
        System.out.println(" > A " + (fraction * 100) + "% fraction of all " + numNodesWithWeight + " weighed nodes is of size " + fractionSize);
        System.out.println(" > Expected number of traffic pairs: " + (((fractionSize % 2 == 0) ? fractionSize : fractionSize - 1) * getANodeWeight()));
        System.out.println(" > Generated truncated Weight-Matching traffic pairs (total amount of pairs: " + ls.size() + ")");

        return ls;

    }

    /**
     * Calculate best pairs given the distance file.
     *
     * @param filename  Distance file
     * @return Best pairs
     */
    private ArrayList<TrafficPair> trafficWeightPairsUsingDistanceFile(String filename) {

        // Execute python script that does maximum weight matching
        Main.runCommand("python python/maxWeight" + Main.PYTHON_VERSION + ".py " + filename + " temp/max_weight_matching.txt");

        // Sort the maximum weight pairs
        Main.runCommand(Main.SORT_COMMAND + " -nr -k3 -k1 temp/max_weight_matching.txt -o temp/max_weight_matching_sorted.txt");

        // Retrieve and return the traffic pairs from the output
        return trafficPairsFromWeightFile("temp/max_weight_matching_sorted.txt");

    }

    /**
     * Read the traffic pairs from a file.
     *
     * @param filename  File location
     * @return  Traffic pairs
     */
    private ArrayList<TrafficPair> trafficPairsFromWeightFile(String filename) {

        ArrayList<TrafficPair> ls = new ArrayList<>();

        try {

            // Open file stream
            FileInputStream fileStream = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));

            // Simply read in the server pairs
            String strLine;
            while ((strLine = br.readLine()) != null) {
                String[] match = strLine.split(" ");
                int svr1 = Integer.parseInt(match[0]);
                int svr2 = Integer.parseInt(match[1]);
                double wt = Double.parseDouble(match[2]);

                // Ignore pairs with weight of zero, as they are to be excluded according to the fraction
                if (wt > 0) {
                    ls.add(new TrafficPair(svr1, svr2));
                }

            }

            // Close stream
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ls;

    }

    /**
     * Print the distances between servers, including the link
     * they have to their respective switches.
     *
     * @param serverDistFile        Destination file
     */
    private void printMaxWeightProblem(String serverDistFile, boolean useFraction, boolean inFraction[]) {

        try {

            // Open output stream
            FileWriter fileStream = new FileWriter(serverDistFile);
            BufferedWriter out = new BufferedWriter(fileStream);

            // Print out the distance between all pairs of servers
            for (int i = 0; i < numNodesWithWeight; i++) {
                for (int j = 0; j < numNodesWithWeight; j++) {

                    // Retrieve true switch positions in the graph
                    int fromSwitch = nodesWithWeight.get(i);
                    int toSwitch = nodesWithWeight.get(j);

                    // Not allowed to have an weighed edge between the same switch (circular edge)
                    if (fromSwitch == toSwitch) {
                        continue;
                    }

                    // Check if it is in the fraction
                    if (!useFraction || (inFraction[i] && inFraction[j])) {

                        // Calculate distance between servers
                        int printDist = shortestPathLen[fromSwitch][toSwitch] + 2; // Plus two because of one link per server to its switch

                        // Write output distance
                        if (isMaxMatching) {
                            out.write(i + " " + j + " " + printDist + "\n");
                        } else {
                            out.write(i + " " + j + " " + (Main.MAX_SHORTEST_PATH_LENGTH_POSSIBLE - printDist) + "\n");
                        }

                    } else { // If it is not in the fraction, prevent it from being chosen
                        out.write(i + " " + j + " " + 0 + "\n");
                    }

                }
            }

            // Close output stream
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
