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

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class RegularSingleServerTrafficTest {

    @Test
    public void testTrafficPairsStride() {
        for (int stride = 1; stride < 500; stride++) {
            if (stride % 231 == 0) {
                continue;
            }
            Graph g = new Graph("test", 231);
            SelectorResult<Traffic> sel = TrafficSelector.select(
                    TrafficSelector.TrafficMode.STRIDE, g,
                    new String[]{"-str", String.valueOf(stride)}
            );
            testTrafficPairStrideValidity(g, sel.getResult().generate(), stride);
        }
    }

    private void testTrafficPairStrideValidity(Graph g, List<TrafficPair> pairs, int stride) {

        assertEquals(g.getNumNodes(), pairs.size());

        boolean[] presentLeft = new boolean[g.getNumNodes()];
        boolean[] presentRight = new boolean[g.getNumNodes()];

        for (TrafficPair tp : pairs) {
            assertFalse(tp.getFrom() == tp.getTo());
            assertEquals((tp.getFrom() + stride) % g.getNumNodes(), tp.getTo());
            presentLeft[tp.getFrom()] = true;
            presentRight[tp.getTo()] = true;
        }

        for (int i = 0; i < g.getNumNodes(); i++) {
            assertTrue(presentLeft[i]);
            assertTrue(presentRight[i]);
        }

    }

    @Test
    public void testTrafficPairsAllToOne() {
        Graph g = new Graph("test", 231);
        Main.initGlobalRandomness(2);

        SelectorResult<Traffic> sel = TrafficSelector.select(
                TrafficSelector.TrafficMode.ALL_TO_ONE, g,
                new String[]{}
        );

        testTrafficPairAllToOneValidity(g, sel.getResult().generate());
    }

    private void testTrafficPairAllToOneValidity(Graph g, List<TrafficPair> pairs) {

        assertEquals(g.getNumNodes() - 1, pairs.size());

        boolean[] present = new boolean[g.getNumNodes()];

        int chosen = -1;
        for (TrafficPair tp : pairs) {
            if (chosen == -1) {
                chosen = tp.getTo();
            }
            assertEquals(chosen, tp.getTo());
            present[tp.getFrom()] = true;
        }

        for (int i = 0; i < g.getNumNodes(); i++) {
            assertTrue(present[i] || chosen == i);
        }

    }

    @Test
    public void testTrafficPairsAllToAll() {
        Graph g = new Graph("test", 231);
        Main.initGlobalRandomness(2);

        SelectorResult<Traffic> sel = TrafficSelector.select(
                TrafficSelector.TrafficMode.ALL_TO_ALL, g,
                new String[]{}
        );

        testTrafficPairAllToAllValidityFraction(g, sel.getResult().generate(), 1.0);
    }

    @Test
    public void testSimpleWeightPairs() {

        // Graph
        TestGraph g = new TestGraph("test", 6);
        g.addBidirNeighbor(0, 1);
        g.addBidirNeighbor(0, 2);
        g.addBidirNeighbor(1, 2);
        g.addBidirNeighbor(2, 3);
        g.addBidirNeighbor(3, 4);
        g.addBidirNeighbor(4, 5);

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
            sum += g.getShortestPathLen()[p.getFrom()][p.getTo()];
        }
        assertEquals(6, sum);

        // Max-weight matching
        sel = TrafficSelector.select(
                TrafficSelector.TrafficMode.MAX_WEIGHT_PAIRS, g,
                new String[]{"-tfr", "1.0"}
        );
        pairs = sel.getResult().generate();

        sum = 0;
        for (TrafficPair p : pairs) {
            sum += g.getShortestPathLen()[p.getFrom()][p.getTo()];
        }
        assertEquals(16, sum);

    }

    @Test
    public void testTrafficRandomPermutationPairsFraction() {

        for (int fraction = 1; fraction <= 10; fraction += 1) {

            for (int n = 20; n < 120; n++) {

                Graph g = new Graph("test", n);

                // Try out some deterministic randomness cases
                for (int i = 1; i < 100; i++) {
                    Main.initGlobalRandomness(i);

                    SelectorResult<Traffic> sel = TrafficSelector.select(
                            TrafficSelector.TrafficMode.RAND_PERM_PAIRS, g,
                            new String[]{"-tfr", String.valueOf(fraction / 10.0)}
                    );

                    testTrafficPairValidityFraction(g, sel.getResult().generate(), fraction / 10.0);
                }

            }

        }

    }

    private void testTrafficPairValidityFraction(Graph g, List<TrafficPair> pairs, double fraction) {

        assertEquals((int) (fraction * g.getNumNodes()), pairs.size());

        boolean[] presentLeft = new boolean[g.getNumNodes()];
        boolean[] presentRight = new boolean[g.getNumNodes()];

        // No identity pairs
        for (TrafficPair tp : pairs) {
            presentLeft[tp.getFrom()] = true;
            presentRight[tp.getTo()] = true;
            assertTrue(tp.getFrom() != tp.getTo());
        }

        // Check all nodes are present on both sides
        for (TrafficPair tp : pairs) {
            assertTrue(presentLeft[tp.getFrom()]);
            assertTrue(presentRight[tp.getTo()]);
        }

    }

    @Test
    public void testTrafficRandomPermutationPairs() {

        for (int n = 2; n < 120; n++) {

            Graph g = new Graph("test", n);

            // Try out some deterministic randomness cases
           for (int i = 1; i < 100; i++) {
               Main.initGlobalRandomness(i);

               SelectorResult<Traffic> sel = TrafficSelector.select(
                       TrafficSelector.TrafficMode.RAND_PERM_PAIRS, g,
                       new String[]{"-tfr", "1.0"}
               );

                testTrafficPairValidity(g, sel.getResult().generate());
            }

        }

    }

    private void testTrafficPairValidity(Graph g, List<TrafficPair> pairs) {

        boolean[] presentLeft = new boolean[g.getNumNodes()];
        boolean[] presentRight = new boolean[g.getNumNodes()];

        // No identity pairs
        for (TrafficPair tp : pairs) {
            presentLeft[tp.getFrom()] = true;
            presentRight[tp.getTo()] = true;
            assertTrue(tp.getFrom() != tp.getTo());
        }

        // Check all nodes are present on both sides
        for (int i = 0; i < pairs.size(); i++) {
            assertTrue(presentLeft[i]);
            assertTrue(presentRight[i]);
        }

    }

    @Test
    public void testTrafficAllToAllFrac() {
        for (int fraction = 1; fraction <= 10; fraction += 1) {

            for (int n = 2; n < 120; n += 13) {

                Graph g = new Graph("test", n);

                // Try out some deterministic randomness cases
                for (int i = 1; i < 50; i++) {
                    Main.initGlobalRandomness(i);

                    SelectorResult<Traffic> sel = TrafficSelector.select(
                            TrafficSelector.TrafficMode.ALL_TO_ALL_FRAC, g,
                            new String[]{"-tfr", String.valueOf(fraction / 10.0)}
                    );

                    testTrafficPairAllToAllValidityFraction(g, sel.getResult().generate(), fraction / 10.0);
                }

            }

        }

    }

    private void testTrafficPairAllToAllValidityFraction(Graph g, List<TrafficPair> pairs, double fraction) {

        int fracSize = (int) (fraction * g.getNumNodes());
        assertEquals(fracSize * (fracSize - 1), pairs.size());

        boolean[] present = new boolean[g.getNumNodes()];

        // No identity pairs
        for (TrafficPair tp : pairs) {
            present[tp.getFrom()] = true;
            present[tp.getTo()] = true;
            //System.out.println(tp.getFrom() + " - " + tp.getTo());
        }

        // Check that every node in the fraction has the 2 * fracSize traffic pairs
        for (int i = 0; i < g.getNumNodes(); i++) {
            if (present[i]) {
                int x = 0;
                int y = 0;
                for (TrafficPair tp : pairs) {
                    if (i == tp.getFrom()) {
                        x++;
                    }
                    if (i == tp.getTo()) {
                        y++;
                    }
                }
                assertEquals(x, fracSize - 1);
                assertEquals(y, fracSize - 1);
            }
        }

    }

    @Test
    public void testTrafficAllToAllFracInOrder() {
        for (int fraction = 1; fraction <= 10; fraction += 1) {

            for (int n = 2; n < 120; n += 13) {

                Graph g = new Graph("test", n);

                // Try out some deterministic randomness cases
                for (int i = 1; i < 50; i++) {
                    Main.initGlobalRandomness(i);

                    SelectorResult<Traffic> sel = TrafficSelector.select(
                            TrafficSelector.TrafficMode.ALL_TO_ALL_FRAC_ORDER, g,
                            new String[]{"-tfr", String.valueOf(fraction / 10.0)}
                    );

                    testTrafficPairAllToAllValidityFractionInOrder(g, sel.getResult().generate(), fraction / 10.0);
                }

            }

        }

    }

    private void testTrafficPairAllToAllValidityFractionInOrder(Graph g, List<TrafficPair> pairs, double fraction) {

        int fracSize = (int) (fraction * g.getNumNodes());
        assertEquals(fracSize * (fracSize - 1), pairs.size());

        boolean[] present = new boolean[g.getNumNodes()];

        // No identity pairs
        for (TrafficPair tp : pairs) {
            present[tp.getFrom()] = true;
            present[tp.getTo()] = true;
        }

        // Check that every node in the fraction has the 2 * fracSize traffic pairs
        for (int i = 0; i < g.getNumNodes(); i++) {

            // It is in order, so it cannot be in the fraction beyond indices fracSize
            assertFalse(i >= fracSize && present[i]);

            if (present[i]) {
                int x = 0;
                int y = 0;
                for (TrafficPair tp : pairs) {
                    if (i == tp.getFrom()) {
                        x++;
                    }
                    if (i == tp.getTo()) {
                        y++;
                    }
                }
                assertEquals(x, fracSize - 1);
                assertEquals(y, fracSize - 1);
            }

        }

    }

}
