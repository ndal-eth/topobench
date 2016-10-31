/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.traffic;

import ch.ethz.topobench.graph.Graph;

import java.util.List;

public abstract class Traffic {

    final int numNodes;
    final int totalWeight;
    final int shortestPathLen[][];
    final Graph graph;
    final List<Integer> nodesWithWeight;
    final int numNodesWithWeight;

    public Traffic(Graph graph) {

        // Initialize variables
        this.graph = graph;
        this.totalWeight = graph.getTotalWeight();
        this.numNodes = graph.getNumNodes();
        this.shortestPathLen = graph.getShortestPathLen();
        this.nodesWithWeight = graph.getNodesWithWeight();
        this.numNodesWithWeight = this.nodesWithWeight.size();

        // Generating traffic for a graph without weight is pointless
        // and is never intended
        if (numNodesWithWeight < 2) {
            throw new IllegalArgumentException(
                    "Less than two nodes (" + numNodesWithWeight + ") have weight " +
                    "thus it is impossible to generate traffic."
            );
        }

    }

    public abstract List<TrafficPair> generate();

    /**
     * Ensure that all nodes in the graph that do have weight,
     * all have the same uniform weight. This typically needs
     * to be ensured with traffic modes that do some form of
     * node-level coupling.
     *
     * For example correct topologies:
     * 0 [w=0], 1 [w=3], 2 [w=3], 3 [w=0]
     * 0 [w=0], 1 [w=0], 2 [w=0], 3 [w=0]
     *
     * For example incorrect topologies:
     * 0 [w=2], 1 [w=3], 2 [w=3], 3 [w=0]
     * 0 [w=0], 1 [w=1], 2 [w=3], 3 [w=0]
     */
    final void ensureNodesWithWeightAreUniform() {
        int weight = -1;
        for (Integer i : nodesWithWeight) {
            if (weight == -1) {
                weight = graph.getNodeWeight(i);
            } else if (weight != graph.getNodeWeight(i)) {
                throw new IllegalArgumentException(
                        "The selected traffic mode can only be run on graphs of which the nodes " +
                        "that do have weight, the weight is uniform among all of them."
                );
            }
        }
    }

    protected int getANodeWeight() {
        return graph.getNodeWeight(nodesWithWeight.get(0));
    }

}
