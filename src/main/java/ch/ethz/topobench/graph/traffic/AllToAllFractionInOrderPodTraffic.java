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
 * to all servers in the fraction (excluding servers attached to its own node and to servers in its own pod).
 */
public class AllToAllFractionInOrderPodTraffic extends Traffic {

    private final int perPod;
    private final double fraction;

    public AllToAllFractionInOrderPodTraffic(Graph graph, int perPod, double fraction) {
        super(graph);
        this.perPod = perPod;
        this.fraction = fraction;
        ensureNodesWithWeightAreUniform();
    }

    /**
     * Find the pod for the corresponding node identifier.
     *
     * @param x     Node identifier
     *
     * @return  Pod it belongs to
     */
    private int findPod(int x) {
        for (int i = x ; i >= 0; i--) {
            if (i % perPod == 0) {
                return i;
            }
        }
        throw new RuntimeException("Could not find pod; presumably because x=" + x + " was negative.");
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
                if (findPod(fromSwitch) == findPod(toSwitch)) {
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
        System.out.println(" > Generated All-to-All ordered server pod traffic pairs");
        System.out.println(" > An ordered fraction " + (int) (100.0 * fraction) + "% of " + numNodesWithWeight + " weighed nodes was selected");
        System.out.println(" > The node fraction is thus of size: " + fractionSize + " nodes.");
        System.out.println(" > Among these a total of " + ls.size() + " server pairs were ultimately generated.");

        return ls;

    }

}
