/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.traffic;

import ch.ethz.topobench.graph.Graph;

import java.util.ArrayList;
import java.util.List;
/**
 * Traffic model where each server in a node
 * talks to another distinct server in a node <i>stride</i>
 * indices away forward sequentially in the node index.
 */
public class StrideTraffic extends Traffic {

    private final int stride;

    public StrideTraffic(Graph graph, int stride) {
        super(graph);
        this.stride = stride;
        ensureNodesWithWeightAreUniform();

        // Check validity of the stride
        if (stride % numNodesWithWeight == 0) {
            throw new IllegalArgumentException(
                    "Cannot have stride n=" + stride + " divisible by " +
                    "the number of server-hosting nodes (=" + numNodesWithWeight + ")."
            );
        }

    }

    @Override
    public List<TrafficPair> generate() {

        // Generate pairs
        ArrayList<TrafficPair> ls = new ArrayList<>();
        for (int i = 0; i < numNodesWithWeight; i++) {
            int j = (i + stride) % numNodesWithWeight;

            // Use mapping to retrieve original nodes
            int fromSwitch = nodesWithWeight.get(i);
            int toSwitch = nodesWithWeight.get(j);

            // Retrieve all servers on both switches
            List<Integer> fromSvrs = graph.switchToSvrs(fromSwitch);
            List<Integer> toSvrs = graph.switchToSvrs(toSwitch);

            // Size of fromSvrs and toSvrs must be equal due to the call
            // in the constructor: ensureNodesWithWeightAreUniform

            // Couple one of the servers of one switch to one server of the other switch
            for (int z = 0; z < fromSvrs.size(); z++) {
                ls.add(new TrafficPair(fromSvrs.get(z), toSvrs.get(z)));
            }

        }

        // Log creation
        System.out.println(" > Generating traffic pairs with stride=" + stride + "...");
        System.out.println(" > " + numNodesWithWeight + "/" + numNodes + " nodes are server-hosting and thus participating.");
        System.out.println(" > Expecting a total number of " + (numNodesWithWeight * graph.getNodeWeight(nodesWithWeight.get(0))) + " traffic pairs.");
        System.out.println(" > Generated Stride server traffic pairs (total: " + ls.size() + ").");

        return ls;

    }

}