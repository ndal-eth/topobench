/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.traffic;

import ch.ethz.topobench.Main;
import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.TestGraph;
import ch.ethz.topobench.graph.graphs.FatTreeSigcomm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class MaxWeightPairsTrafficTest {

    // Handy debug line:
    // System.out.println(g.svrToSwitch(p.getFrom()) + " - " + g.svrToSwitch(p.getTo()));

    @Test
    public void testRingFraction() {

        // Traffic generator
        Main.loadRunEnvironmentConfiguration();

        // Graph
        TestGraph g = new TestGraph("test", 40, 3);
        for (int i = 0; i < 40; i++) { // Ring of forty nodes
            g.addBidirNeighbor(i, (i + 1) % 40);
        }
        for (int i = 0; i < 20; i++) { // Set all even node indices without weight ('servers')
            g.setNodeWeight(i * 2, 0);
        }

        for (int i = 10; i <= 100; i += 5) {

            double fraction = ((double) i) / 100.0;
            int fractionSize = (int) (fraction * 20);
            if (fractionSize % 2 != 0) {
                fractionSize -= 1;
            }

            // Min-weight matching
            SelectorResult<Traffic> sel = TrafficSelector.select(
                    TrafficSelector.TrafficMode.MIN_WEIGHT_PAIRS, g,
                    new String[]{"-tfr", String.valueOf(fraction)}
            );
            List<TrafficPair> pairs = sel.getResult().generate();
            int sum = 0;
            for (TrafficPair p : pairs) {
                sum += g.getShortestPathLen()[g.svrToSwitch(p.getFrom())][g.svrToSwitch(p.getTo())];
            }
            assertEquals(fractionSize * 3, pairs.size()); // <fracSize> are active
            assertEquals(2 * fractionSize * 3, sum); // Every node consumes 2 path and has 3 servers
            checkPairValidity(g, pairs, 3);

            // Max-weight matching
            sel = TrafficSelector.select(
                    TrafficSelector.TrafficMode.MAX_WEIGHT_PAIRS, g,
                    new String[]{"-tfr", String.valueOf(fraction)}
            );
            pairs = sel.getResult().generate();

            sum = 0;
            for (TrafficPair p : pairs) {
                sum += g.getShortestPathLen()[g.svrToSwitch(p.getFrom())][g.svrToSwitch(p.getTo())];
            }
            assertEquals(fractionSize * 3, pairs.size()); // <fracSize> active
            assertEquals(20 * fractionSize * 3, sum); // Every directed pair consumes 20 path, as the pairs will opposite in the ring
            checkPairValidity(g, pairs, 3);

        }

    }

    @Test
    public void testFatTree() {

        // Graph
        Graph g = new FatTreeSigcomm(8); // Supports 128 servers, has 80 nodes

        // Traffic generator
        Main.loadRunEnvironmentConfiguration();

        // Min-weight matching
        SelectorResult<Traffic> sel = TrafficSelector.select(
                TrafficSelector.TrafficMode.MIN_WEIGHT_PAIRS, g,
                new String[]{"-tfr", "1.0"}
        );
        List<TrafficPair> pairs = sel.getResult().generate();
        int sum = 0;
        for (TrafficPair p : pairs) {
            sum += g.getShortestPathLen()[g.svrToSwitch(p.getFrom())][g.svrToSwitch(p.getTo())];
        }
        assertEquals(128, pairs.size()); // 64 + 64 node pairs
        assertEquals(2 * 128, sum); // Every directed traffic pair consumes 2 path, as the pairs will be inside the same pod
        checkPairValidity(g, pairs, 4);

        // Max-weight matching
        sel = TrafficSelector.select(
                TrafficSelector.TrafficMode.MAX_WEIGHT_PAIRS, g,
                new String[]{"-tfr", "1.0"}
        );
        pairs = sel.getResult().generate();

        sum = 0;
        for (TrafficPair p : pairs) {
            sum += g.getShortestPathLen()[g.svrToSwitch(p.getFrom())][g.svrToSwitch(p.getTo())];
        }
        assertEquals(128, pairs.size()); // 64 + 64 node pairs
        assertEquals(4 * 128, sum); // Every directed pair consumes 4 path, as the pairs will be across pods
        checkPairValidity(g, pairs, 4);

    }

    @Test
    public void testSmallOneWithoutEven() {

        // Graph
        TestGraph g = new TestGraph("test", 6, 3);
        g.addBidirNeighbor(0, 1);
        g.addBidirNeighbor(0, 2);
        g.addBidirNeighbor(1, 2);
        g.addBidirNeighbor(2, 3);
        g.addBidirNeighbor(3, 4);
        g.addBidirNeighbor(4, 5);
        g.setNodeWeight(3, 0);
        g.setNodeWeight(5, 0);

        // 0-1
        // |/
        // 2-3-4-5

        // Traffic generator
        Main.loadRunEnvironmentConfiguration();

        // Min-weight matching
        SelectorResult<Traffic> sel = TrafficSelector.select(
                TrafficSelector.TrafficMode.MIN_WEIGHT_PAIRS, g,
                new String[]{"-tfr", "1.0"}
        );
        List<TrafficPair> pairs = sel.getResult().generate();
        int sum = 0;
        for (TrafficPair p : pairs) {
            sum += g.getShortestPathLen()[g.svrToSwitch(p.getFrom())][g.svrToSwitch(p.getTo())];
        }
        assertEquals(18, sum); // 2-4, 4-2, 0-1, 1-0
        checkPairValidity(g, pairs, 3);

        // Max-weight matching
        sel = TrafficSelector.select(
                TrafficSelector.TrafficMode.MAX_WEIGHT_PAIRS, g,
                new String[]{"-tfr", "1.0"}
        );
        pairs = sel.getResult().generate();

        sum = 0;
        for (TrafficPair p : pairs) {
            sum += g.getShortestPathLen()[g.svrToSwitch(p.getFrom())][g.svrToSwitch(p.getTo())];
        }
        assertEquals(24, sum); // 0-4, 4-0, 1-2, 2-1
        checkPairValidity(g, pairs, 3);

    }

    @Test
    public void testSmallOneWithoutUneven() {

        // Graph
        TestGraph g = new TestGraph("test", 6, 3);
        g.addBidirNeighbor(0, 1);
        g.addBidirNeighbor(0, 2);
        g.addBidirNeighbor(1, 2);
        g.addBidirNeighbor(2, 3);
        g.addBidirNeighbor(3, 4);
        g.addBidirNeighbor(4, 5);
        g.setNodeWeight(3, 0);

        // 0-1
        // |/
        // 2-3-4-5

        // Traffic generator
        Main.loadRunEnvironmentConfiguration();

        // Min-weight matching
        SelectorResult<Traffic> sel = TrafficSelector.select(
                TrafficSelector.TrafficMode.MIN_WEIGHT_PAIRS, g,
                new String[]{"-tfr", "1.0"}
        );
        List<TrafficPair> pairs = sel.getResult().generate();
        int sum = 0;
        for (TrafficPair p : pairs) {
            sum += g.getShortestPathLen()[g.svrToSwitch(p.getFrom())][g.svrToSwitch(p.getTo())];
        }
        assertEquals(12, sum); // 4-5, 5-4, 2-0, 0-2
        checkPairValidity(g, pairs, 3);

        // Max-weight matching
        sel = TrafficSelector.select(
                TrafficSelector.TrafficMode.MAX_WEIGHT_PAIRS, g,
                new String[]{"-tfr", "1.0"}
        );
        pairs = sel.getResult().generate();

        sum = 0;
        for (TrafficPair p : pairs) {
            sum += g.getShortestPathLen()[g.svrToSwitch(p.getFrom())][g.svrToSwitch(p.getTo())];
        }
        assertEquals(42, sum); // 0-5, 5-0, 1-4, 4-1
        checkPairValidity(g, pairs, 3);

    }

    private void checkPairValidity(Graph graph, List<TrafficPair> pairs, int w) {

        // First, identify nodes involved in the permutation
        Set<Integer> involvedNodes = new HashSet<>();
        for (TrafficPair p : pairs) {
            involvedNodes.add(graph.svrToSwitch(p.getFrom()));
            involvedNodes.add(graph.svrToSwitch(p.getTo()));
            assertTrue(p.getFrom() != p.getTo());
            assertFalse(graph.svrToSwitch(p.getFrom()) == graph.svrToSwitch(p.getTo()));
        }

        // Go over each of the involved nodes in the fraction
        for (Integer node : involvedNodes) {

            int otherSwitch = -1;
            Set<Integer> coupledSvrs = new HashSet<>();
            for (Integer svr : graph.switchToSvrs(node)) {

                // Make sure each server is only matched to one other server
                Set<Integer> matchedServers = new HashSet<>();
                for (TrafficPair p : pairs) {
                    if (p.getFrom() == svr) {
                        matchedServers.add(p.getTo());
                        coupledSvrs.add(p.getTo());

                        // Make sure it is only coupled to a single other switch
                        if (otherSwitch == -1) {
                            otherSwitch = graph.svrToSwitch(p.getTo());
                        }
                        assertTrue(otherSwitch == graph.svrToSwitch(p.getTo()));

                    }
                }
                assertEquals(1, matchedServers.size());

            }
            assertEquals(w, coupledSvrs.size());

        }

    }

}
