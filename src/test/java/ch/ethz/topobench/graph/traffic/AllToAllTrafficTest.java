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
import org.junit.Assert;
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
public class AllToAllTrafficTest {

    @Test
    public void testTrafficPairsAllToAll() {

        for (int w = 1; w < 20; w++) {

            TestGraph g = new TestGraph("test", 18);
            g.setNodeWeight(0, 0);
            g.setNodeWeight(1, w);
            g.setNodeWeight(2, 0);
            g.setNodeWeight(3, w);
            g.setNodeWeight(4, w);
            g.setNodeWeight(5, 0);
            g.setNodeWeight(6, w);
            g.setNodeWeight(7, w);
            g.setNodeWeight(8, 0);
            g.setNodeWeight(9, w);
            g.setNodeWeight(10, w);
            g.setNodeWeight(11, 0);
            g.setNodeWeight(12, w);
            g.setNodeWeight(13, 0);
            g.setNodeWeight(14, 0);
            g.setNodeWeight(15, 0);
            g.setNodeWeight(16, w);
            g.setNodeWeight(17, w);

            SelectorResult<Traffic> sel = TrafficSelector.select(
                    TrafficSelector.TrafficMode.ALL_TO_ALL, g,
                    new String[]{}
            );

            testTrafficPairAllToAllValidityFraction(g, sel.getResult().generate(), 1.0, w);

        }

    }

    @Test
    public void testTrafficPairsAllToAllFraction() {

        Main.initGlobalRandomness(2);

        for (int w = 1; w < 20; w++) {

            TestGraph g = new TestGraph("test", 18);
            g.setNodeWeight(0, 0);
            g.setNodeWeight(1, w);
            g.setNodeWeight(2, 0);
            g.setNodeWeight(3, w);
            g.setNodeWeight(4, w);
            g.setNodeWeight(5, 0);
            g.setNodeWeight(6, w);
            g.setNodeWeight(7, w);
            g.setNodeWeight(8, 0);
            g.setNodeWeight(9, w);
            g.setNodeWeight(10, w);
            g.setNodeWeight(11, 0);
            g.setNodeWeight(12, w);
            g.setNodeWeight(13, 0);
            g.setNodeWeight(14, 0);
            g.setNodeWeight(15, 0);
            g.setNodeWeight(16, w);
            g.setNodeWeight(17, w);

            for (int i = 1; i <= 10; i++) {

                SelectorResult<Traffic> sel = TrafficSelector.select(
                        TrafficSelector.TrafficMode.ALL_TO_ALL_FRAC, g,
                        new String[]{"-tfr", String.valueOf(((double) i) / 10.0)}
                );

                testTrafficPairAllToAllValidityFraction(g, sel.getResult().generate(), ((double) i) / 10.0, w);

            }

        }

    }

    @Test
    public void testTrafficPairsAllToAllFractionInOrder() {

        Main.initGlobalRandomness(2);

        for (int w = 1; w < 20; w++) {

            TestGraph g = new TestGraph("test", 18);
            g.setNodeWeight(0, 0);
            g.setNodeWeight(1, w);
            g.setNodeWeight(2, 0);
            g.setNodeWeight(3, w);
            g.setNodeWeight(4, w);
            g.setNodeWeight(5, 0);
            g.setNodeWeight(6, w);
            g.setNodeWeight(7, w);
            g.setNodeWeight(8, 0);
            g.setNodeWeight(9, w);
            g.setNodeWeight(10, w);
            g.setNodeWeight(11, 0);
            g.setNodeWeight(12, w);
            g.setNodeWeight(13, 0);
            g.setNodeWeight(14, 0);
            g.setNodeWeight(15, 0);
            g.setNodeWeight(16, w);
            g.setNodeWeight(17, w);

            for (int i = 1; i <= 10; i++) {

                SelectorResult<Traffic> sel = TrafficSelector.select(
                        TrafficSelector.TrafficMode.ALL_TO_ALL_FRAC_ORDER, g,
                        new String[]{"-tfr", String.valueOf(((double) i) / 10.0)}
                );

                testTrafficPairAllToAllValidityFraction(g, sel.getResult().generate(), ((double) i) / 10.0, w);

            }

        }

    }

    private void testTrafficPairAllToAllValidityFraction(Graph g, List<TrafficPair> pairs, double fraction, int uniformWeight) {


        List<Integer> nodesWithWeight = g.getNodesWithWeight();
        int numNodesWithWeight = nodesWithWeight.size();

        int fracSize = (int) (fraction * numNodesWithWeight);
        assertEquals((fracSize * uniformWeight) * ((fracSize - 1) * uniformWeight), pairs.size());

        boolean[] present = new boolean[g.getNumNodes()];

        // No identity pairs
        for (TrafficPair tp : pairs) {
            int swFrom = g.svrToSwitch(tp.getFrom());
            int swTo = g.svrToSwitch(tp.getTo());
            present[swFrom] = true;
            present[swTo] = true;
            assertFalse(swFrom == swTo);
            assertTrue(g.getNodeWeight(swFrom) == uniformWeight);
            assertTrue(g.getNodeWeight(swTo) == uniformWeight);
        }

        // Check that every node in the fraction has its to and from traffic pairs
        for (int i = 0; i < numNodesWithWeight; i++) {
            int realIndex = nodesWithWeight.get(i);

            if (present[realIndex]) {
                int x = 0;
                int y = 0;
                for (TrafficPair tp : pairs) {
                    if (realIndex == g.svrToSwitch(tp.getFrom())) {
                        x++;
                    }
                    if (realIndex == g.svrToSwitch(tp.getTo())) {
                        y++;
                    }
                }
                assertEquals(uniformWeight * (fracSize - 1) * uniformWeight, x);
                assertEquals(uniformWeight * (fracSize - 1) * uniformWeight, y);
            }
        }

        // First, identify nodes involved in the permutation
        Set<Integer> involvedNodes = new HashSet<>();
        for (TrafficPair p : pairs) {
            involvedNodes.add(g.svrToSwitch(p.getFrom()));
            involvedNodes.add(g.svrToSwitch(p.getTo()));
        }

        for (Integer node : involvedNodes) {

            for (Integer svr : g.switchToSvrs(node)) {

                Set<Integer> matchedServers = new HashSet<>();
                for (TrafficPair p : pairs) {
                    if (p.getFrom() == svr) {
                        matchedServers.add(p.getTo());
                        assertTrue(node != g.svrToSwitch(p.getTo())); // To a different switch
                    }
                }
                Assert.assertEquals((involvedNodes.size() - 1) * uniformWeight, matchedServers.size());

            }

        }

    }

}
