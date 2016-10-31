/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.analysis;

import ch.ethz.topobench.AnalyzeSolution;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AnalyzeSimpleLPFlow {

    public static void main(String[] args) {
        printAnalysis();
    }

    public static void printAnalysis() {
        List<Tuple> tuples = readTuples();
        printFlowAll(tuples);
        printFlowOriginNode(tuples);
        printSummary(tuples);
    }

    private static class Tuple {

        public final int i;
        public final int j;
        public final double flow;

        Tuple(int i, int j, double flow) {
            this.i = i;
            this.j = j;
            this.flow = flow;
        }

    }

    /**
     * Print a simple summary of the node-to-node flows.
     *
     * @param tuples    List of tuples (i, j, flow)
     */
    private static void printSummary(List<Tuple> tuples) {

        try {

            // Open output stream
            FileWriter fileStream = new FileWriter(AnalyzeSolution.ANALYSIS_FOLDER + "/simple_results_flow_summary.txt");
            BufferedWriter out = new BufferedWriter(fileStream);

            // Determine number of nodes in the graph
            int n = ((int) Math.sqrt(tuples.size())) + 1;

            Tuple min = new Tuple(-1, -1, Double.MAX_VALUE);
            double minSum = Double.MAX_VALUE;
            int minSumI = -1;
            for (int i = 0; i < n - 1; i++) {
                double sum = 0.0;
                for (int j = 0; j < n - 1; j++) {
                    Tuple t = tuples.get(i * (n - 1) + j);
                    sum += t.flow;
                    if (t.flow < min.flow) {
                        min = t;
                    }
                }
                if (sum < minSum) {
                    minSum = sum;
                    minSumI = i;
                }
            }

            out.write("SUMMARY OF " + tuples.size() + " NODE-TO-NODE-FLOW TUPLES\n");
            out.write("-----------------------------------------------------------\n\n");
            out.write("Lowest directed flow between two nodes: " + min.i + " to " + min.j + " with flow " + min.flow + "\n");
            out.write("Node with the lowest flow from it: " + minSumI + " with total outgoing flow " + minSum);

            // Close output stream
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Print all tuples in separate file.
     *
     * @param tuples    List of tuples (i, j, flow)
     */
    private static void printFlowAll(List<Tuple> tuples) {

        try {

            // Open output stream
            FileWriter fileStream = new FileWriter(AnalyzeSolution.ANALYSIS_FOLDER + "/simple_results_flow_all.txt");
            BufferedWriter out = new BufferedWriter(fileStream);

            for (Tuple t : tuples) {
                out.write(t.i + " " + t.j + " " + t.flow + "\n");
            }

            // Close output stream
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Print the flow originating from each node.
     *
     * @param tuples    List of tuples (i, j, flow)
     */
    private static void printFlowOriginNode(List<Tuple> tuples) {

        try {

            // Open output stream
            FileWriter fileStream = new FileWriter(AnalyzeSolution.ANALYSIS_FOLDER + "/simple_results_flow_origin_node.txt");
            BufferedWriter out = new BufferedWriter(fileStream);

            // Determine number of nodes in the graph
            int n = ((int) Math.sqrt(tuples.size())) + 1;

            for (int i = 0; i < n - 1; i++) {
                double sum = 0.0;
                for (int j = 0; j < n - 1; j++) {
                    sum += tuples.get(i * (n - 1) + j).flow;
                }
                out.write(i + " " + sum + "\n");
            }

            // Close output stream
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Read all the node-to-node flow tuples in the file temp/lp/vector.sol
     *
     * @return  List of tuples (i, j, flow)
     */
    private static List<Tuple> readTuples() {

        try {

            // Storage
            List<Tuple> tuples = new ArrayList<>();

            // Open input stream
            FileInputStream fileStream = new FileInputStream("temp/vector.sol");
            BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));

            // Simply read in the node pairs
            String strLine;
            while ((strLine = br.readLine()) != null) {

                // Structure of line must be: f_i_j flow
                if (!strLine.startsWith("f_")) {
                    continue;
                }
                strLine = strLine.replaceAll("f_", "").replaceAll("_", " ");

                // Split up sentences
                String[] match = strLine.split(" ");
                tuples.add(new Tuple(Integer.parseInt(match[0]), Integer.parseInt(match[1]), Double.parseDouble(match[2])));

            }

            // Close input stream
            br.close();

            return tuples;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

}
