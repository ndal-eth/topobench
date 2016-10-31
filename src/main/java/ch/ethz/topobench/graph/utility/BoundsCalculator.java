/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.utility;

public class BoundsCalculator {

    public static void main(String[] args) {
        for (int N = 9; N <= 90; N += 9) {
            // int N = 6;
            int d = 5;
            int serversPerSwitch = 10;
            System.out.println("Average PL min. bound: " + avgPathLengthMinBound(N, d));
            System.out.println("Maximum A2A total throughput per node: " + maxThroughputPerNode(N, d));
            System.out.println("Maximum A2A total throughput per server: " + maxThroughputPerServer(N, d, serversPerSwitch));
        }
    }

    /**
     * Compute the average path length minimum bound for any possible
     * (bi-directional) graph of N nodes, with each node having degree d.
     *
     * @param N     Number of nodes
     * @param d     Degree of each node
     *
     * @return  Minimum average path length bound
     */
    public static double avgPathLengthMinBound(int N, int d) {

        // Find maximum k for which inequality R >= 0 holds
        double R = -1, curR = 0.0;
        int k = 0;
        while (curR >= 0.0) {

            // Calculate R value
            curR = (N - 1);
            for (int j = 1; j <= k - 1; j++) {
                curR -= d * Math.pow(d - 1, j - 1);
            }

            // Invalid R means we've reached the ceiling
            if (curR < 0.0) {
                k = k - 1;
                break;
            } else {
                R = curR;
            }

            // Onto next k
            k++;

        }

        // Just in case it doesn't work out at all
        if (R < 0.0) {
            throw new RuntimeException("Impossible parameters given; no value of k can make R positive.");
        }

        // Calculate average path length min. bound
        double sum = k * R;
        for (int j = 1; j <= k - 1; j++){
            sum += j * d * (int)Math.pow(d - 1, j - 1);
        }
        sum /= (double) (N - 1);

        return sum;

    }

    public static double maxThroughputPerNode(int N, int d) {
        return ((double) d) / (avgPathLengthMinBound(N, d));
    }

    public static double maxThroughputPerServer(int N, int d, int serversPerSwitch) {
        return ((double) d) / (avgPathLengthMinBound(N, d) * serversPerSwitch);
    }

}
