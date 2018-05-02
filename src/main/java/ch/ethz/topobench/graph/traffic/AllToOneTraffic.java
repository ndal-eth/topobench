/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.traffic;

import ch.ethz.topobench.Main;
import ch.ethz.topobench.graph.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Traffic model from all servers to some random server.
 *
 * Given m servers over n nodes, it creates all pairs of the m
 * servers to a single other server under the condition that no
 * pair is on the same node.
 */
public class AllToOneTraffic extends Traffic {

    public AllToOneTraffic(Graph graph) {
        super(graph);
    }

    @Override
    public List<TrafficPair> generate() {

        // Determine target server
        int target = Main.universalRand.nextInt(totalWeight);

        // For each other server, create pair to target
        ArrayList<TrafficPair> ls = new ArrayList<>();
        for (int from = 0; from < totalWeight; from++) {
            if (graph.svrToSwitch(from) != graph.svrToSwitch(target)) {
                ls.add(new TrafficPair(from, target));
            }
        }

        // Log creation
        System.out.println(" > Generated All-to-One server traffic pairs (total: " + ls.size() + ")");

        return ls;

    }

}
