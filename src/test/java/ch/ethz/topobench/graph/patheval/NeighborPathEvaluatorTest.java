/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.patheval;

import ch.ethz.topobench.graph.TestGraph;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NeighborPathEvaluatorTest {

    private TestGraph g, g2;

    @Before
    public void setup() {

        // Graph
        //
        //   1-4
        //   | |\
        // 2-0-6-7
        //   | |
        //   3-5

        g = new TestGraph("test", 8);
        g.addBidirNeighbor(0, 1);
        g.addBidirNeighbor(0, 2);
        g.addBidirNeighbor(0, 3);
        g.addBidirNeighbor(0, 6);
        g.addBidirNeighbor(1, 4);
        g.addBidirNeighbor(3, 5);
        g.addBidirNeighbor(4, 6);
        g.addBidirNeighbor(5, 6);
        g.addBidirNeighbor(6, 7);
        g.addBidirNeighbor(4, 7);

        g.calculateShortestPaths();

        // Graph 2
        //
        //   1-4
        //   | |\
        // 2-0-6-7
        //   |/|
        //   3-5

        g2 = new TestGraph("test2", 8);
        g2.addBidirNeighbor(0, 1);
        g2.addBidirNeighbor(0, 2);
        g2.addBidirNeighbor(0, 3);
        g2.addBidirNeighbor(0, 6);
        g2.addBidirNeighbor(1, 4);
        g2.addBidirNeighbor(3, 5);
        g2.addBidirNeighbor(4, 6);
        g2.addBidirNeighbor(5, 6);
        g2.addBidirNeighbor(6, 7);
        g2.addBidirNeighbor(4, 7);
        g2.addBidirNeighbor(3, 6);

        g2.calculateShortestPaths();

    }

    @Test
    public void testG1() {

        // ONLY DIRECT PATHS FROM NEIGHBORS
        PathEvaluator eval = PathEvaluatorSelector.select(
                PathEvaluatorSelector.Type.NEIGHBOR, g,
                new String[]{}
        ).getResult();

        // 0 -> 6
        assertFalse(  eval.isFlowZero(0, 6, 0, 1));
        assertFalse(  eval.isFlowZero(0, 6, 1, 0));
        assertFalse(  eval.isFlowZero(0, 6, 0, 2));
        assertFalse(  eval.isFlowZero(0, 6, 2, 0));
        assertFalse(  eval.isFlowZero(0, 6, 0, 3));
        assertFalse(  eval.isFlowZero(0, 6, 3, 0));
        assertFalse( eval.isFlowZero(0, 6, 0, 6));
        assertTrue(  eval.isFlowZero(0, 6, 6, 0));
        assertFalse(  eval.isFlowZero(0, 6, 1, 4));
        assertTrue(  eval.isFlowZero(0, 6, 4, 1));
        assertFalse(  eval.isFlowZero(0, 6, 3, 5));
        assertTrue(  eval.isFlowZero(0, 6, 5, 3));
        assertFalse(  eval.isFlowZero(0, 6, 4, 6));
        assertTrue(  eval.isFlowZero(0, 6, 6, 4));
        assertFalse(  eval.isFlowZero(0, 6, 5, 6));
        assertTrue(  eval.isFlowZero(0, 6, 6, 5));
        assertTrue(  eval.isFlowZero(0, 6, 6, 7));
        assertTrue(  eval.isFlowZero(0, 6, 7, 6));
        assertTrue(  eval.isFlowZero(0, 6, 4, 7));
        assertTrue(  eval.isFlowZero(0, 6, 7, 4));

    }

    @Test
    public void testG2() {

        // ONLY DIRECT PATHS FROM NEIGHBORS
        PathEvaluator eval = PathEvaluatorSelector.select(
                PathEvaluatorSelector.Type.NEIGHBOR, g2,
                new String[]{}
        ).getResult();

        // 7 -> 0
        assertTrue(  eval.isFlowZero(7, 0, 0, 1));
        assertFalse(  eval.isFlowZero(7, 0, 1, 0));
        assertTrue(  eval.isFlowZero(7, 0, 0, 2));
        assertTrue(  eval.isFlowZero(7, 0, 2, 0));
        assertTrue(  eval.isFlowZero(7, 0, 0, 3));
        assertTrue(  eval.isFlowZero(7, 0, 3, 0));
        assertTrue( eval.isFlowZero(7, 0, 0, 6));
        assertFalse(  eval.isFlowZero(7, 0, 6, 0));
        assertTrue(  eval.isFlowZero(7, 0, 1, 4));
        assertFalse(  eval.isFlowZero(7, 0, 4, 1));
        assertTrue(  eval.isFlowZero(7, 0, 3, 5));
        assertTrue(  eval.isFlowZero(7, 0, 5, 3));
        assertFalse(  eval.isFlowZero(7, 0, 4, 6));
        assertTrue(  eval.isFlowZero(7, 0, 6, 4));
        assertTrue(  eval.isFlowZero(7, 0, 5, 6));
        assertTrue(  eval.isFlowZero(7, 0, 6, 5));
        assertTrue(  eval.isFlowZero(7, 0, 6, 7));
        assertFalse(  eval.isFlowZero(7, 0, 7, 6));
        assertTrue(  eval.isFlowZero(7, 0, 4, 7));
        assertFalse(  eval.isFlowZero(7, 0, 7, 4));
        assertTrue(  eval.isFlowZero(7, 0, 3, 6));
        assertTrue(  eval.isFlowZero(7, 0, 6, 3));

    }

}
