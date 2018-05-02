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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class StrideTrafficTest {

    @Test
    public void testIllegalArguments() {

        boolean thrown = false;

        TestGraph g = new TestGraph("test", 18);
        g.setNodeWeight(0, 2);
        g.setNodeWeight(1, 2);
        g.setNodeWeight(2, 0);
        g.setNodeWeight(3, 0);
        g.setNodeWeight(4, 2);
        g.setNodeWeight(5, 0);
        g.setNodeWeight(6, 2);
        g.setNodeWeight(7, 2);
        g.setNodeWeight(8, 0);
        g.setNodeWeight(9, 2);
        g.setNodeWeight(10, 2);
        g.setNodeWeight(11, 0);
        g.setNodeWeight(12, 2);
        g.setNodeWeight(13, 0);
        g.setNodeWeight(14, 0);
        g.setNodeWeight(15, 0);
        g.setNodeWeight(16, 2);
        g.setNodeWeight(17, 2);

        try {
            SelectorResult<Traffic> sel = TrafficSelector.select(
                    TrafficSelector.TrafficMode.STRIDE, g,
                    new String[]{"-stride", String.valueOf(0)}
            );

            sel.getResult().generate();
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown = false;

        try {
            SelectorResult<Traffic> sel = TrafficSelector.select(
                    TrafficSelector.TrafficMode.STRIDE, g,
                    new String[]{"-stride", String.valueOf(10)}
            );

            sel.getResult().generate();
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown = false;

        TestGraph g2 = new TestGraph("test", 2);
        g2.setNodeWeight(0, 0);
        g2.setNodeWeight(1, 0);

        try {
            SelectorResult<Traffic> sel = TrafficSelector.select(
                    TrafficSelector.TrafficMode.STRIDE, g2,
                    new String[]{"-stride", String.valueOf(1)}
            );

            sel.getResult().generate();
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

    }

    @Test
    public void testTrafficPairsStride() {

        Main.initGlobalRandomness(2);

        for (int w = 1; w < 20; w++) {

            TestGraph g = new TestGraph("test", 36);
            g.setNodeWeight(0, w);
            g.setNodeWeight(1, w);
            g.setNodeWeight(2, 0);
            g.setNodeWeight(3, 0);
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
            g.setNodeWeight(18, w);
            g.setNodeWeight(19, w);
            g.setNodeWeight(20, 0);
            g.setNodeWeight(21, 0);
            g.setNodeWeight(22, w);
            g.setNodeWeight(23, 0);
            g.setNodeWeight(24, w);
            g.setNodeWeight(25, w);
            g.setNodeWeight(26, 0);
            g.setNodeWeight(27, w);
            g.setNodeWeight(28, w);
            g.setNodeWeight(29, 0);
            g.setNodeWeight(30, w);
            g.setNodeWeight(31, 0);
            g.setNodeWeight(32, 0);
            g.setNodeWeight(33, 0);
            g.setNodeWeight(34, w);
            g.setNodeWeight(35, w);


            for (int i = 0; i <= 300; i++) {

                if (i % 20 == 0) {
                    continue;
                }

                SelectorResult<Traffic> sel = TrafficSelector.select(
                        TrafficSelector.TrafficMode.STRIDE, g,
                        new String[]{"-stride", String.valueOf(i)}
                );

                validate(g, sel.getResult().generate(), i, w);

            }

        }

    }

    public void validate(Graph g, List<TrafficPair> pairs, int stride, int uniformWeight) {

        List<Integer> nodesWithWeight = g.getNodesWithWeight();
        int numNodesWithWeight = nodesWithWeight.size();

        // Ensure that the size is corresponding
        assertEquals(g.getNodesWithWeight().size() * uniformWeight, pairs.size());

        // Go over each of the involved nodes in the fraction
        for (int node : nodesWithWeight) {

            int otherSwitch = -1;
            for (Integer svr : g.switchToSvrs(node)) {

                // Make sure each server is only matched to one other server
                Set<Integer> matchedServers = new HashSet<>();
                for (TrafficPair p : pairs) {
                    if (p.getFrom() == svr) {
                        matchedServers.add(p.getTo());

                        // Make sure it is only coupled to a single other switch
                        if (otherSwitch == -1) {
                            otherSwitch = g.svrToSwitch(p.getTo());
                        }
                        assertTrue(otherSwitch == g.svrToSwitch(p.getTo()));

                        // Make sure that the stride is correct
                        int idx1 = -1;
                        int idx2 = -2;
                        for (int i = 0; i < nodesWithWeight.size(); i++) {
                            if (node == nodesWithWeight.get(i)) {
                                idx1 = i;
                            }
                            if (g.svrToSwitch(p.getTo()) == nodesWithWeight.get(i)) {
                                idx2 = i;
                            }
                        }
                        assertEquals((idx1 + stride) % numNodesWithWeight, idx2);

                    }
                }
                assertEquals(1, matchedServers.size());

            }

        }

    }

}
