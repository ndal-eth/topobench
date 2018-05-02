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

public class AnalyzeSimpleLPLinkCap {

    public static void main(String[] args) {
        printAnalysis();
    }

    public static void printAnalysis() {
        List<Tuple> tuples = readTuples();
        printLinkFlowAll(tuples);
        printLinkSum(tuples);
    }

    private static class Tuple {

        public final int i;
        public final int j;
        public final int k;
        public final double flow;

        Tuple(int i, int j, int k, double flow) {
            this.i = i;
            this.j = j;
            this.k = k;
            this.flow = flow;
        }

    }

    /**
     * Print all tuples in separate file temp/lp/results_link_all.txt.
     *
     * @param tuples    List of tuples (i, j, k, flow)
     */
    private static void printLinkFlowAll(List<Tuple> tuples) {

        try {

            // Open output stream
            FileWriter fileStream = new FileWriter(AnalyzeSolution.ANALYSIS_FOLDER + "/simple_results_link_all.txt");
            BufferedWriter out = new BufferedWriter(fileStream);

            for (Tuple t : tuples) {
                out.write(t.i + " " + t.j + " " + t.k + " " + t.flow + "\n");
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
    private static void printLinkSum(List<Tuple> tuples) {

        try {

            // Open output stream
            FileWriter fileStream = new FileWriter(AnalyzeSolution.ANALYSIS_FOLDER + "/simple_results_link_summed.txt");
            BufferedWriter out = new BufferedWriter(fileStream);

            int curI = -1;
            int curJ = -1;
            double sum = 0.0;
            for (Tuple t : tuples) {
                if (t.i != curI || t.j != curJ) {

                    // Write sum of the flow from all k for (i, j)
                    if (curI != -1 && curJ != -1) {
                        out.write(curI + " " + curJ + " " + sum + "\n");
                    }

                    // Reset
                    sum = 0.0;
                    curI = t.i;
                    curJ = t.j;
                }
                sum += t.flow;
            }

            // Print out final sum of last link
            if (curI != -1 && curJ != -1) {
                out.write(curI + " " + curJ + " " + sum + "\n");
            }

            // Close output stream
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Read all the edge (i, j) flows contributed by node k tuples in the file temp/lp/vector.sol
     *
     * @return  List of tuples (i, j, k, flow)
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
                if (!strLine.startsWith("l_")) {
                    continue;
                }
                strLine = strLine.replaceAll("l_", "").replaceAll("_", " ");

                // Split up sentences
                String[] match = strLine.split(" ");
                tuples.add(new Tuple(
                        Integer.parseInt(match[0]),
                        Integer.parseInt(match[1]),
                        Integer.parseInt(match[2]),
                        Double.parseDouble(match[3])
                ));

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
