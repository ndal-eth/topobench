/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.patheval;

import ch.ethz.topobench.graph.Link;
import ch.ethz.topobench.graph.Graph;

import java.io.BufferedWriter;
import java.io.FileWriter;

class GraphCompatibility {

    /**
     * Print the graph such that it is interpretable by the Dijkstra/Yen's algorithm library.
     *
     * @param graph     Input graph
     * @param fileName  File name
     */
    static void printGraphToFile(Graph graph, String fileName) {

        try {

            // Calculate shortest paths
            graph.calculateShortestPaths();

            // Open output stream
            FileWriter fileStream = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(fileStream);

            out.write(graph.getNumNodes() + "\n" + "\n");

            for (int i = 0; i < graph.getNumNodes(); i++) {
                for (int j = 0; j < graph.getAdjacencyList()[i].size(); j++) {
                    if (i != 0 || j != 0) {
                        out.write("\n");
                    }
                    Link link = graph.getAdjacencyList()[i].get(j);
                    out.write(i + " " + link.getLinkTo() + " 1");
                }
            }

            // Close output stream
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
