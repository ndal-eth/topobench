/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.graphs;

import ch.ethz.topobench.Main;
import ch.ethz.topobench.graph.Graph;

import java.io.*;

import static ch.ethz.topobench.Main.runCommand;

public class XpanderGraph extends Graph {

    private final int k;
    private final int d;
    private final int s;

    /**
     * Construct an Xpander graph.
     *
     * @param d     Network ports per switch (graph degree)
     * @param s     Server ports per switch
     * @param n     Number of switches desired
     *
     */
    public XpanderGraph(int d, int s, int n) {
        super("Xpander", ((int) Math.ceil(((double) n) / ((double) (d + 1))) * (d + 1)), s);

        // OTHER OPTION: USING FIXED K, DO MULTIPLE LIFTS
        // To be satisfied: d * k^lifts >= n
        // As such lifts = ceil(log_k(n / d))
        // int lifts = (int) Math.ceil(Math.log(((double) n) / ((double) d)) / Math.log(k));

        // Set parameters
        this.d = d;
        this.k = (int) Math.ceil(((double) n) / ((double) (d + 1)));
        this.s = s;

        // Setup uniform weight
        this.setupUniformWeight(s);

        if (Main.universalRand == null) {
            throw new RuntimeException("XpanderGraph: constructor: missing the universal RNG from ProduceLP");
        }

        try {

            // Write parameters
            FileWriter fileStreamIn = new FileWriter("temp/xpander_in.temp");
            BufferedWriter out = new BufferedWriter(fileStreamIn);
            out.write(this.d + "\n" + this.k + "\n" + Main.getRandomSeed());
            out.close();

            // Run python script
            runCommand("python python/xpanderGen" + Main.PYTHON_VERSION + ".py temp/xpander_in.temp temp/xpander_out.temp");

            // Open file stream
            FileInputStream fileStreamOut = new FileInputStream("temp/xpander_out.temp");
            BufferedReader br = new BufferedReader(new InputStreamReader(fileStreamOut));

            // Simply read in the server pairs
            String strLine;
            while ((strLine = br.readLine()) != null) {
                String[] values = strLine.split(" ");
                int src = Integer.parseInt(values[0]);
                int des = Integer.parseInt(values[1]);
                this.addBidirNeighbor(src, des);
            }

            // Close stream
            br.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(
                "Created Xpander graph with " +
                "d=" + this.d + " network ports per switch, " +
                "s=" + this.s + " server ports per switch" +
                " and " + numNodes + " nodes using a k=" + this.k + "-lift");

    }

    /**
     * Get the degree of switches to connect to other switches.
     *
     * @return  Degree
     */
    public int getD() {
        return d;
    }

    /**
     * Get the parameter k of the k-lift performed.
     *
     * @return k-parameter
     */
    public int getK() {
        return k;
    }

    /**
     * Get the weight per node.
     *
     * @return  Weight (number of servers) per node
     */
    public int getS() {
        return s;
    }

}
