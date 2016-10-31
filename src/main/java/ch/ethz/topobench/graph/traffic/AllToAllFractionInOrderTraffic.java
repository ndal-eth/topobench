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
 * Traffic model from all nodes to all for a deterministically
 * chosen fraction (%) of the server-hosting nodes existing in the graph.
 *
 * This is deterministically done, because it assumes it takes the fraction linearly with the indexes.
 *
 * Within the selected fraction of nodes, every server in each node sends
 * to all servers in the fraction (excluding servers attached to its own node).
 */
public class AllToAllFractionInOrderTraffic extends Traffic {

    private final double fraction;

    public AllToAllFractionInOrderTraffic(Graph graph, double fraction) {
        super(graph);
        this.fraction = fraction;
        ensureNodesWithWeightAreUniform();
    }

    @Override
    public List<TrafficPair> generate() {

        // Size of the fraction
        int fractionSize = (int) (numNodesWithWeight * fraction);

        // Every node to every other node inside the fraction
        ArrayList<TrafficPair> ls = new ArrayList<>();
        for (int i = 0; i < fractionSize; i++) {
            for (int j = 0; j < fractionSize; j++) {

                // Mapping of server-hosting nodes
                int fromSwitch = nodesWithWeight.get(i);
                int toSwitch = nodesWithWeight.get(j);

                // Cannot have traffic on the same switch
                if (fromSwitch == toSwitch) {
                    continue;
                }

                // Couple each of the servers of one switch to every other server of the other switch
                for (Integer fromSvr : graph.switchToSvrs(fromSwitch)) {
                    for (Integer toSvr : graph.switchToSvrs(toSwitch)) {
                        ls.add(new TrafficPair(fromSvr, toSvr));
                    }
                }

            }
        }

        // Log creation
        System.out.println(" > Generated All-to-All ordered server traffic pairs");
        System.out.println(" > An ordered fraction " + (int) (100.0 * fraction) + "% of " + numNodes + " nodes was selected");
        System.out.println(" > The node fraction is thus of size: " + fractionSize + " nodes.");
        System.out.println(" > Among these a total of " + ls.size() + " server pairs were ultimately generated.");

        return ls;

    }

}
