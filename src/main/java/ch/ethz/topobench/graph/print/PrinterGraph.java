/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */


package ch.ethz.topobench.graph.print;

import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.Link;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class PrinterGraph {

    private Graph graph;

    public PrinterGraph(Graph graph) {
        this.graph = graph;
    }

    /**
     * Print the graph to a file.
     *
     * @param fileName  File destination
     */
    public void print(String fileName) {

        try {

            // Calculate shortest paths
            graph.calculateShortestPaths();

            // Open output stream
            FileWriter fileStream = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(fileStream);

            for (int i = 0; i < graph.getNumNodes(); i++) {
                for (int j = 0; j < graph.getAdjacencyList()[i].size(); j++) {
                    if (i != 0 || j != 0) {
                        out.write("\n");
                    }
                    Link link = graph.getAdjacencyList()[i].get(j);
                    out.write(i + " " + link.getLinkTo());
                }
            }

            // Close output stream
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
