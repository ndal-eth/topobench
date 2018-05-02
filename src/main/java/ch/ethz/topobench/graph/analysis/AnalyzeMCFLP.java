/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.analysis;

import ch.ethz.topobench.AnalyzeSolution;

import java.io.*;
import java.util.*;

public class AnalyzeMCFLP {

    public static void main(String[] args) {
        printAnalysis();
    }

    public static void printAnalysis() {
        List<Tuple> tuples = readTuples();
        printFlowAll(tuples);
        printFlowOriginNode(tuples);
        printFlowPerLink(tuples);
    }

    private static class Tuple {

        public final int k;
        public final int i;
        public final int j;
        public final double flow;

        // How much flow with fid k sent over link (i, j)
        Tuple(int k, int i, int j, double flow) {
            this.k = k;
            this.i = i;
            this.j = j;
            this.flow = flow;
        }

    }

    private static class TupleSum {

        final int first;
        final int second;
        final double flow;

        TupleSum(int first, int second, double flow) {
            this.first = first;
            this.second = second;
            this.flow = flow;
        }
    }

    /**
     * Print all tuples in separate file.
     *
     * @param tuples    List of tuples (k, i, j, flow)
     */
    private static void printFlowAll(List<Tuple> tuples) {

        try {

            // Open output stream
            FileWriter fileStream = new FileWriter(AnalyzeSolution.ANALYSIS_FOLDER + "/mcf_results_flow_all.txt");
            BufferedWriter out = new BufferedWriter(fileStream);

            for (Tuple t : tuples) {
                out.write(t.k + " " + t.i + " " + t.j + " " + t.flow + "\n");
            }

            // Close output stream
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Print the flow going over each edge (i, j).
     *
     * @param tuples    List of tuples (k, i, j, flow)
     */
    private static void printFlowPerLink(List<Tuple> tuples) {

        try {

            // Open output stream
            FileWriter fileStream = new FileWriter(AnalyzeSolution.ANALYSIS_FOLDER + "/mcf_results_link_summed.txt");
            BufferedWriter out = new BufferedWriter(fileStream);

            // Save the sum for every link
            Map<AbstractMap.SimpleEntry<Integer, Integer>, Double> linkSum = new HashMap<>();
            for (Tuple t : tuples) {
                AbstractMap.SimpleEntry<Integer, Integer> p = new AbstractMap.SimpleEntry<>(t.i, t.j);
                double sum = linkSum.get(p) != null ? linkSum.get(p) : 0.0;
                linkSum.put(p, sum + t.flow);
            }

            // Convert to list such that there is order
            List<TupleSum> sumTuples = new ArrayList<>();
            for (AbstractMap.SimpleEntry<Integer, Integer> p : linkSum.keySet()) {
                sumTuples.add(new TupleSum(p.getKey(), p.getValue(), linkSum.get(p)));
            }

            // Sort first by link source i, then by link destination j
            Collections.sort(sumTuples, new Comparator<TupleSum>() {
                public int compare(TupleSum o1, TupleSum o2) {
                    int dI = o1.first - o2.first;
                    if (dI == 0) {
                        return o1.second - o2.second;
                    }
                    return dI;
                }
            });

            // Print per directed link (i, j) the sum
            for (TupleSum ts : sumTuples) {
                out.write(ts.first + " " + ts.second + " " + ts.flow + "\n");
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
     * @param tuples    List of tuples (k, i, j, flow)
     */
    private static void printFlowOriginNode(List<Tuple> tuples) {

        try {

            // Open output stream
            FileWriter fileStream = new FileWriter(AnalyzeSolution.ANALYSIS_FOLDER + "/mcf_results_flow_origin_node.txt");
            BufferedWriter out = new BufferedWriter(fileStream);

            // Sum up in map
            Map<AbstractMap.SimpleEntry<Integer, Integer>, Double> linkSum = new HashMap<AbstractMap.SimpleEntry<Integer, Integer>, Double>();
            for (Tuple t : tuples) {

                Double sum = linkSum.get(new AbstractMap.SimpleEntry<>(t.k, t.i));
                if (sum == null) {
                    linkSum.put(new AbstractMap.SimpleEntry<>(t.k, t.i), t.flow);
                } else {
                    linkSum.put(new AbstractMap.SimpleEntry<>(t.k, t.i), sum + t.flow);
                }

            }

            // Convert into list so that there is order
            List<TupleSum> sumTuples = new ArrayList<>();
            for (AbstractMap.SimpleEntry<Integer, Integer> p : linkSum.keySet()) {
                sumTuples.add(new TupleSum(p.getKey(), p.getValue(), linkSum.get(p)));
            }

            // Sort first by origin node i, then by fid k
            Collections.sort(sumTuples, new Comparator<TupleSum>() {
                public int compare(TupleSum o1, TupleSum o2) {
                    int dI = o1.second - o2.second;
                    if (dI == 0) {
                        return o1.first - o2.first;
                    }
                    return dI;
                }
            });

            // Print out the maximum flow that node i sends out (presumably that would be the flow originating from itself)
            int currentI = -1;
            double maxSoFar = Double.MIN_VALUE;
            for (TupleSum ts : sumTuples) {
                if (currentI != ts.second) {
                    if (currentI != -1) {
                        out.write(currentI + " " + maxSoFar + "\n");
                        maxSoFar = Double.MIN_VALUE;
                    }
                    currentI = ts.second;
                }
                maxSoFar = Math.max(ts.flow, maxSoFar);
            }
            if (currentI != -1) {
                out.write(currentI + " " + maxSoFar + "\n");
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
     * Each tuple: "how much does the flow with fid k affect link (i, j)"
     *
     * @return  List of tuples (k, i, j, flow)
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

                // Split up sentence
                String[] match = strLine.split(" ");
                tuples.add(new Tuple(Integer.parseInt(match[0]), Integer.parseInt(match[1]), Integer.parseInt(match[2]), Double.parseDouble(match[3])));

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
