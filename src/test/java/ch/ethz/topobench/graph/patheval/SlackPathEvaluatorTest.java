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

public class SlackPathEvaluatorTest {

    private TestGraph g;

    @Before
    public void setup() {

        // Graph
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

    }

    @Test
    public void testSlack0() {

        // ONLY DIRECT PATHS
        PathEvaluator eval = PathEvaluatorSelector.select(
                PathEvaluatorSelector.Type.SLACK, g,
                new String[]{"-slk", "0"}
        ).getResult();

        // 0 -> 6
        assertTrue(  eval.isFlowZero(0, 6, 0, 1));
        assertTrue(  eval.isFlowZero(0, 6, 1, 0));
        assertTrue(  eval.isFlowZero(0, 6, 0, 2));
        assertTrue(  eval.isFlowZero(0, 6, 2, 0));
        assertTrue(  eval.isFlowZero(0, 6, 0, 3));
        assertTrue(  eval.isFlowZero(0, 6, 3, 0));
        assertFalse( eval.isFlowZero(0, 6, 0, 6));
        assertTrue(  eval.isFlowZero(0, 6, 6, 0));
        assertTrue(  eval.isFlowZero(0, 6, 1, 4));
        assertTrue(  eval.isFlowZero(0, 6, 4, 1));
        assertTrue(  eval.isFlowZero(0, 6, 3, 5));
        assertTrue(  eval.isFlowZero(0, 6, 5, 3));
        assertTrue(  eval.isFlowZero(0, 6, 4, 6));
        assertTrue(  eval.isFlowZero(0, 6, 6, 4));
        assertTrue(  eval.isFlowZero(0, 6, 5, 6));
        assertTrue(  eval.isFlowZero(0, 6, 6, 5));
        assertTrue(  eval.isFlowZero(0, 6, 6, 7));
        assertTrue(  eval.isFlowZero(0, 6, 7, 6));
        assertTrue(  eval.isFlowZero(0, 6, 4, 7));
        assertTrue(  eval.isFlowZero(0, 6, 7, 4));

        // 3 -> 6
        assertTrue(  eval.isFlowZero(3, 6, 0, 1));
        assertTrue(  eval.isFlowZero(3, 6, 1, 0));
        assertTrue(  eval.isFlowZero(3, 6, 0, 2));
        assertTrue(  eval.isFlowZero(3, 6, 2, 0));
        assertTrue(  eval.isFlowZero(3, 6, 0, 3));
        assertFalse( eval.isFlowZero(3, 6, 3, 0));
        assertFalse( eval.isFlowZero(3, 6, 0, 6));
        assertTrue(  eval.isFlowZero(3, 6, 6, 0));
        assertTrue(  eval.isFlowZero(3, 6, 1, 4));
        assertTrue(  eval.isFlowZero(3, 6, 4, 1));
        assertFalse( eval.isFlowZero(3, 6, 3, 5));
        assertTrue(  eval.isFlowZero(3, 6, 5, 3));
        assertTrue(  eval.isFlowZero(3, 6, 4, 6));
        assertTrue(  eval.isFlowZero(3, 6, 6, 4));
        assertFalse( eval.isFlowZero(3, 6, 5, 6));
        assertTrue(  eval.isFlowZero(3, 6, 6, 5));
        assertTrue(  eval.isFlowZero(3, 6, 6, 7));
        assertTrue(  eval.isFlowZero(3, 6, 7, 6));
        assertTrue(  eval.isFlowZero(0, 6, 4, 7));
        assertTrue(  eval.isFlowZero(0, 6, 7, 4));

        // 7 -> 4
        assertTrue(  eval.isFlowZero(7, 4, 0, 1));
        assertTrue(  eval.isFlowZero(7, 4, 1, 0));
        assertTrue(  eval.isFlowZero(7, 4, 0, 2));
        assertTrue(  eval.isFlowZero(7, 4, 2, 0));
        assertTrue(  eval.isFlowZero(7, 4, 0, 3));
        assertTrue(  eval.isFlowZero(7, 4, 3, 0));
        assertTrue(  eval.isFlowZero(7, 4, 0, 6));
        assertTrue(  eval.isFlowZero(7, 4, 6, 0));
        assertTrue(  eval.isFlowZero(7, 4, 1, 4));
        assertTrue(  eval.isFlowZero(7, 4, 4, 1));
        assertTrue(  eval.isFlowZero(7, 4, 3, 5));
        assertTrue(  eval.isFlowZero(7, 4, 5, 3));
        assertTrue(  eval.isFlowZero(7, 4, 4, 6));
        assertTrue(  eval.isFlowZero(7, 4, 6, 4));
        assertTrue(  eval.isFlowZero(7, 4, 5, 6));
        assertTrue(  eval.isFlowZero(7, 4, 6, 5));
        assertTrue(  eval.isFlowZero(7, 4, 6, 7));
        assertTrue(  eval.isFlowZero(7, 4, 7, 6));
        assertTrue(  eval.isFlowZero(7, 4, 4, 7));
        assertFalse( eval.isFlowZero(7, 4, 7, 4));

        // 6 -> 1
        assertFalse(  eval.isFlowZero(6, 1, 0, 1));
        assertTrue(  eval.isFlowZero(6, 1, 1, 0));
        assertTrue(  eval.isFlowZero(6, 1, 0, 2));
        assertTrue(  eval.isFlowZero(6, 1, 2, 0));
        assertTrue(  eval.isFlowZero(6, 1, 0, 3));
        assertTrue(  eval.isFlowZero(6, 1, 3, 0));
        assertTrue(  eval.isFlowZero(6, 1, 0, 6));
        assertFalse(  eval.isFlowZero(6, 1, 6, 0));
        assertTrue(  eval.isFlowZero(6, 1, 1, 4));
        assertFalse(  eval.isFlowZero(6, 1, 4, 1));
        assertTrue(  eval.isFlowZero(6, 1, 3, 5));
        assertTrue(  eval.isFlowZero(6, 1, 5, 3));
        assertTrue(  eval.isFlowZero(6, 1, 4, 6));
        assertFalse( eval.isFlowZero(6, 1, 6, 4));
        assertTrue(  eval.isFlowZero(6, 1, 5, 6));
        assertTrue(  eval.isFlowZero(6, 1, 6, 5));
        assertTrue(  eval.isFlowZero(6, 1, 6, 7));
        assertTrue( eval.isFlowZero(6, 1, 7, 6));
        assertTrue(  eval.isFlowZero(6, 1, 4, 7));
        assertTrue( eval.isFlowZero(6, 1, 7, 4));

    }

    @Test
    public void testSlack1() {

        // ONLY DIRECT PATHS AND PATHS THAT DEVIATE 1
        PathEvaluator eval = PathEvaluatorSelector.select(
                PathEvaluatorSelector.Type.SLACK, g,
                new String[]{"-slack", "1"}
        ).getResult();

        // 7 -> 4
        assertTrue(  eval.isFlowZero(7, 4, 0, 1));
        assertTrue(  eval.isFlowZero(7, 4, 1, 0));
        assertTrue(  eval.isFlowZero(7, 4, 0, 2));
        assertTrue(  eval.isFlowZero(7, 4, 2, 0));
        assertTrue(  eval.isFlowZero(7, 4, 0, 3));
        assertTrue(  eval.isFlowZero(7, 4, 3, 0));
        assertTrue(  eval.isFlowZero(7, 4, 0, 6));
        assertTrue(  eval.isFlowZero(7, 4, 6, 0));
        assertTrue(  eval.isFlowZero(7, 4, 1, 4));
        assertTrue(  eval.isFlowZero(7, 4, 4, 1));
        assertTrue(  eval.isFlowZero(7, 4, 3, 5));
        assertTrue(  eval.isFlowZero(7, 4, 5, 3));
        assertTrue(  eval.isFlowZero(7, 4, 4, 6));
        assertFalse( eval.isFlowZero(7, 4, 6, 4));
        assertTrue(  eval.isFlowZero(7, 4, 5, 6));
        assertTrue(  eval.isFlowZero(7, 4, 6, 5));
        assertTrue(  eval.isFlowZero(7, 4, 6, 7));
        assertFalse( eval.isFlowZero(7, 4, 7, 6));
        assertTrue(  eval.isFlowZero(7, 4, 4, 7));
        assertFalse( eval.isFlowZero(7, 4, 7, 4));

        // 6 -> 1
        assertFalse(  eval.isFlowZero(6, 1, 0, 1));
        assertTrue(  eval.isFlowZero(6, 1, 1, 0));
        assertTrue(  eval.isFlowZero(6, 1, 0, 2));
        assertTrue(  eval.isFlowZero(6, 1, 2, 0));
        assertTrue(  eval.isFlowZero(6, 1, 0, 3));
        assertTrue(  eval.isFlowZero(6, 1, 3, 0));
        assertTrue(  eval.isFlowZero(6, 1, 0, 6));
        assertFalse(  eval.isFlowZero(6, 1, 6, 0));
        assertTrue(  eval.isFlowZero(6, 1, 1, 4));
        assertFalse(  eval.isFlowZero(6, 1, 4, 1));
        assertTrue(  eval.isFlowZero(6, 1, 3, 5));
        assertTrue(  eval.isFlowZero(6, 1, 5, 3));
        assertTrue(  eval.isFlowZero(6, 1, 4, 6));
        assertFalse( eval.isFlowZero(6, 1, 6, 4));
        assertTrue(  eval.isFlowZero(6, 1, 5, 6));
        assertTrue(  eval.isFlowZero(6, 1, 6, 5));
        assertFalse(  eval.isFlowZero(6, 1, 6, 7));
        assertTrue( eval.isFlowZero(6, 1, 7, 6));
        assertTrue(  eval.isFlowZero(6, 1, 4, 7));
        assertFalse( eval.isFlowZero(6, 1, 7, 4));

    }

    @Test
    public void testSlack2() {

        // ALL DIRECT PATHS, PATHS THAT DEVIATE 1 AND PATHS THAT DEVIATE 2
        PathEvaluator eval = PathEvaluatorSelector.select(
                PathEvaluatorSelector.Type.SLACK, g,
                new String[]{"-slack", "2"}
        ).getResult();

        // 7 -> 4
        assertTrue(  eval.isFlowZero(7, 4, 0, 1));
        assertTrue(  eval.isFlowZero(7, 4, 1, 0));
        assertTrue(  eval.isFlowZero(7, 4, 0, 2));
        assertTrue(  eval.isFlowZero(7, 4, 2, 0));
        assertTrue(  eval.isFlowZero(7, 4, 0, 3));
        assertTrue(  eval.isFlowZero(7, 4, 3, 0));
        assertTrue(  eval.isFlowZero(7, 4, 0, 6));
        assertTrue(  eval.isFlowZero(7, 4, 6, 0));
        assertFalse(  eval.isFlowZero(7, 4, 1, 4));
        assertFalse(  eval.isFlowZero(7, 4, 4, 1));
        assertTrue(  eval.isFlowZero(7, 4, 3, 5));
        assertTrue(  eval.isFlowZero(7, 4, 5, 3));
        assertFalse(  eval.isFlowZero(7, 4, 4, 6));
        assertFalse( eval.isFlowZero(7, 4, 6, 4));
        assertTrue(  eval.isFlowZero(7, 4, 5, 6));
        assertTrue(  eval.isFlowZero(7, 4, 6, 5));
        assertFalse(  eval.isFlowZero(7, 4, 6, 7));
        assertFalse( eval.isFlowZero(7, 4, 7, 6));
        assertFalse(  eval.isFlowZero(7, 4, 4, 7));
        assertFalse( eval.isFlowZero(7, 4, 7, 4));

    }

    @Test
    public void testSlack3() {

        // ALL DIRECT PATHS, AND ONES DEVIATING UNTIL INCL. 3
        PathEvaluator eval = PathEvaluatorSelector.select(
                PathEvaluatorSelector.Type.SLACK, g,
                new String[]{"-slk", "3"}
        ).getResult();

        // 7 -> 4
        assertFalse(  eval.isFlowZero(7, 4, 0, 1));
        assertTrue(  eval.isFlowZero(7, 4, 1, 0));
        assertTrue(  eval.isFlowZero(7, 4, 0, 2));
        assertTrue(  eval.isFlowZero(7, 4, 2, 0));
        assertTrue(  eval.isFlowZero(7, 4, 0, 3));
        assertTrue(  eval.isFlowZero(7, 4, 3, 0));
        assertFalse(  eval.isFlowZero(7, 4, 0, 6));
        assertFalse(  eval.isFlowZero(7, 4, 6, 0));
        assertFalse(  eval.isFlowZero(7, 4, 1, 4));
        assertFalse(  eval.isFlowZero(7, 4, 4, 1));
        assertTrue(  eval.isFlowZero(7, 4, 3, 5));
        assertTrue(  eval.isFlowZero(7, 4, 5, 3));
        assertFalse(  eval.isFlowZero(7, 4, 4, 6));
        assertFalse( eval.isFlowZero(7, 4, 6, 4));
        assertFalse(  eval.isFlowZero(7, 4, 5, 6));
        assertFalse(  eval.isFlowZero(7, 4, 6, 5));
        assertFalse(  eval.isFlowZero(7, 4, 6, 7));
        assertFalse( eval.isFlowZero(7, 4, 7, 6));
        assertFalse(  eval.isFlowZero(7, 4, 4, 7));
        assertFalse( eval.isFlowZero(7, 4, 7, 4));

    }

    @Test
    public void testG2() {

        // ONLY DIRECT PATHS AND ONES THAT DEVIATE 1
        PathEvaluator eval = PathEvaluatorSelector.select(
                PathEvaluatorSelector.Type.SLACK, g,
                new String[]{"-slack", "1"}
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
        assertFalse(  eval.isFlowZero(7, 0, 6, 3));

    }

}
