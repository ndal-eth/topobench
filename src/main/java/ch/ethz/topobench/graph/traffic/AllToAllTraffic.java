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
 * Traffic model from all servers to all servers.
 *
 * Given m servers over n nodes, it creates all pairs of the m
 * servers under the condition that no pair is on the same node.
 */
public class AllToAllTraffic extends Traffic {

    public AllToAllTraffic(Graph graph) {
        super(graph);
    }

    @Override
    public List<TrafficPair> generate() {

        // Every server to every other server
        ArrayList<TrafficPair> ls = new ArrayList<>();
        for (int from = 0; from < totalWeight; from++) {
            for (int to = 0; to < totalWeight; to++) {
                if (graph.svrToSwitch(from) != graph.svrToSwitch(to)) {
                    ls.add(new TrafficPair(from, to));
                }
            }
        }

        // Log creation
        System.out.println(" > Generated All-to-All server traffic pairs (total: " + ls.size() + ")");

        return ls;

    }

}
