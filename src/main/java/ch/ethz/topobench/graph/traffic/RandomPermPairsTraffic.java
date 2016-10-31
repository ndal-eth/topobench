/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.traffic;

import ch.ethz.topobench.Main;
import ch.ethz.topobench.graph.Graph;

import java.util.*;

/**
 * Traffic model of random permutation pairs of a
 * randomly chosen fraction (%) of server-hosting nodes.
 *
 * Within a randomly chosen pair of nodes, every single
 * server of the source node sends to one other distinct
 * server of the target node.
 */
public class RandomPermPairsTraffic extends Traffic {

    private final double fraction;

    public RandomPermPairsTraffic(Graph graph, double fraction) {
        super(graph);
        this.fraction = fraction;
        ensureNodesWithWeightAreUniform();
    }

    @Override
    public List<TrafficPair> generate() {

        int fractionSize = (int) (numNodesWithWeight * fraction);

        // Create random mapping such that only a selection is taken
        List<Integer> randList = new ArrayList<>();
        for (int i = 0; i < numNodesWithWeight; i++) {
            randList.add(i);
        }
        Collections.shuffle(randList, Main.universalRand);
        Map<Integer, Integer> chosenMap = new HashMap<>();
        for (int i = 0; i < fractionSize; i++) {
            chosenMap.put(i, randList.get(i));
        }

        // Create a random permutation mapping of the fraction size and then
        // map all the indices to the randomly chosen selection
        List<AbstractMap.SimpleEntry<Integer, Integer>> pairs = trafficRandomPermutationPairsOfSize(fractionSize);
        ArrayList<TrafficPair> ls = new ArrayList<>();
        for (AbstractMap.SimpleEntry<Integer, Integer> p : pairs) {

            // Retrieve from- and to-switch from the random mapping
            int fromSwitch = nodesWithWeight.get(chosenMap.get(p.getKey()));
            int toSwitch = nodesWithWeight.get(chosenMap.get(p.getValue()));

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

        // Log creation
        System.out.println(" > Generated Random-Permutation traffic pairs");
        System.out.println(" > An random fraction " + (int) (100.0 * fraction) + "% of " + numNodes + " nodes was selected");
        System.out.println(" > The node fraction is thus of size: " + fractionSize + " nodes.");
        System.out.println(" > Among these a total of " + ls.size() + " server pairs were ultimately generated.");

        // Return random permutation
        return ls;

    }

    /**
     * Retrieve random permutation pairs for the given n.
     * It is not possible to have a pair (i, j) with i = j.
     *
     * Example (n=5): 0-3, 1-2, 2-0, 3-4, 4-1
     *
     * @param n Number of random permutations to generate
     *
     * @return  List of random pairs; throws exception if impossible (only at n=1)
     */
    private static List<AbstractMap.SimpleEntry<Integer, Integer>> trafficRandomPermutationPairsOfSize(int n) {

        ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> ls = new ArrayList<>();

        // Base cases
        if (n == 0) {
            return ls;
        } else if (n == 1) {
            throw new IllegalArgumentException("Impossible size of n=1; cannot generate random permutation pairs.");
        } else if (n == 2) {
            ls.add(new AbstractMap.SimpleEntry<>(0, 1));
            ls.add(new AbstractMap.SimpleEntry<>(1, 0));
            return ls;
        }

        // Start with matching of all to themselves (identity)
        int[] pairs = new int[n];
        for (int i = 0; i < n; i++) {
            pairs[i] = i;
        }

        // Remaining nodes
        for (int i = 0; i < n; i++) {

            // Choose next one to swap the value at i with
            int k = (i == pairs[i]) ? Main.universalRand.nextInt(n - 1) : Main.universalRand.nextInt(n - 2);

            // Go over each possible swap position
            for (int j = 0; j < n; j++) {

                // It can never be switched back to its start identity place
                if (pairs[i] != j && i != pairs[j]) {

                    // If we've found the next swap
                    if (k == 0) {
                        int temp = pairs[i];
                        pairs[i] = pairs[j];
                        pairs[j] = temp;
                        break;
                    }
                    k--;

                }

            }
        }

        // Finally construct traffic pairs in list
        for (int i = 0; i < n; i++) {
            ls.add(new AbstractMap.SimpleEntry<>(i, pairs[i]));
        }

        return ls;

    }

}
