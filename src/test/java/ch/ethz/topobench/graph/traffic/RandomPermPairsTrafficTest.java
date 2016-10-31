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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class RandomPermPairsTrafficTest {


    @Test
    public void testTrafficPairsRandomPerm() {

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


            for (int i = 1; i <= 10; i++) {

                SelectorResult<Traffic> sel = TrafficSelector.select(
                        TrafficSelector.TrafficMode.RAND_PERM_PAIRS, g,
                        new String[]{"-tfr", String.valueOf(((double) i) / 10.0)}
                );

                validate(g, sel.getResult().generate(), ((double) i) / 10.0, w);

            }

        }

    }

    public void validate(Graph g, List<TrafficPair> pairs, double fraction, int uniformWeight) {

        // First, identify nodes involved in the permutation
        Set<Integer> involvedNodes = new HashSet<>();
        for (TrafficPair p : pairs) {
            involvedNodes.add(g.svrToSwitch(p.getFrom()));
            involvedNodes.add(g.svrToSwitch(p.getTo()));
            assertTrue(p.getFrom() != p.getTo());
            assertFalse(g.svrToSwitch(p.getFrom()) == g.svrToSwitch(p.getTo()));
        }

        // Ensure that the size is corresponding
        assertEquals((int) (fraction * g.getNodesWithWeight().size()), involvedNodes.size());

        // Go over each of the involved nodes in the fraction
        for (Integer node : involvedNodes) {

            int otherSwitch = -1;
            Set<Integer> coupledSvrs = new HashSet<>();
            for (Integer svr : g.switchToSvrs(node)) {

                // Make sure each server is only matched to one other server
                Set<Integer> matchedServers = new HashSet<>();
                for (TrafficPair p : pairs) {
                    if (p.getFrom() == svr) {
                        matchedServers.add(p.getTo());
                        coupledSvrs.add(p.getTo());

                        // Make sure it is only coupled to a single other switch
                        if (otherSwitch == -1) {
                            otherSwitch = g.svrToSwitch(p.getTo());
                        }
                        assertTrue(otherSwitch == g.svrToSwitch(p.getTo()));

                    }
                }
                assertEquals(1, matchedServers.size());

            }
            assertEquals(uniformWeight, coupledSvrs.size());

        }

    }

}
