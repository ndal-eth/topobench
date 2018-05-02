/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.print;

import ch.ethz.topobench.graph.utility.BoundsCalculator;
import ch.ethz.topobench.graph.Graph;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class PrinterPathLengths {

    private final int numNodes;
    private final Graph graph;
    private final int shortestPathLen[][];

    public PrinterPathLengths(Graph graph) {
        this.graph = graph;
        this.numNodes = graph.getNumNodes();
        this.shortestPathLen = graph.getShortestPathLen();
    }

    public void print(String filePath) {

        try {

            // Calculate paths
            graph.calculateShortestPaths();

            // Open stream
            FileWriter fileStream = new FileWriter(filePath);
            BufferedWriter out = new BufferedWriter(fileStream);

            // Calculate
            double sum = 0.0;
            for (int i = 0; i < numNodes; i++) {
                for (int j = 0; j < numNodes; j++) {
                    sum += shortestPathLen[i][j];
                }
            }

            // Show all graph statistics
            out.write("GENERAL STATISTICS:");
            out.write("\nTotal node pairs: " + (numNodes * numNodes));
            out.write("\nAverage path length for node pairs: " + (sum / (numNodes * numNodes)));

            // For regular graph online with uniform weights
            int d = graph.getAdjacencyList()[0].size();
            int w = graph.getNodeWeight(0);
            out.write("\n\nUNDER ASSUMPTION OF REGULAR GRAPH with n=" + numNodes + ", d=" + d + ":");
            out.write("\nMinimum bound on average path length for node pairs: " + BoundsCalculator.avgPathLengthMinBound(numNodes, d));
            out.write("\nMaximum A2A total throughput per node: " + BoundsCalculator.maxThroughputPerNode(numNodes, d));
            out.write("\nMaximum A2A total throughput per server: " + BoundsCalculator.maxThroughputPerServer(numNodes, d, w));

            // Print path lengths
            out.write("\n\nPATH LENGTH FOR NODE PAIRS:\n");
            for (int i = 0; i < numNodes; i++) {
                for (int j = 0; j < numNodes; j++) {
                    out.write(i + " " + j + " " + shortestPathLen[i][j] + "\n");
                }
            }

            // Close stream
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
