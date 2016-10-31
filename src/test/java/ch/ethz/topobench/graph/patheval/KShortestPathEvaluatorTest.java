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

import static org.junit.Assert.assertTrue;

public class KShortestPathEvaluatorTest {

    private TestGraph g;

    @Before
    public void setup() {

        // Graph
        //
        //   1-4
        //   | |\
        // 2-0-6-7
        //   |/|
        //   3-5

        g = new TestGraph("test2", 8);
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
        g.addBidirNeighbor(3, 6);

    }

    @Test
    public void testK1() {

        PathEvaluator eval = PathEvaluatorSelector.select(
                PathEvaluatorSelector.Type.K_SHORTEST_PATHS, g,
                new String[]{ "-ksp", "1", "-peprep", "1"}
        ).getResult();

        // 0 -> 6
        assertTrue(matchValidity(eval, 0, 6,
                new L[]{
                        new L(0, 6)
                },
                new L[]{
                        new L(0, 1), new L(1, 0),
                        new L(0, 2), new L(2, 0),
                        new L(0, 3), new L(3, 0),
                        new L(6, 0),
                        new L(1, 4), new L(4, 1),
                        new L(3, 5), new L(5, 3),
                        new L(4, 6), new L(6, 4),
                        new L(5, 6), new L(6, 5),
                        new L(6, 7), new L(7, 6),
                        new L(4, 7), new L(7, 4),
                        new L(3, 6), new L(6, 3)
                }
         ));

        // 3 -> 7
        assertTrue(matchValidity(eval, 3, 7,
                new L[]{
                        new L(3, 6), new L(6, 7)
                },
                new L[]{
                        new L(0, 1), new L(1, 0),
                        new L(0, 2), new L(2, 0),
                        new L(0, 3), new L(3, 0),
                        new L(0, 6), new L(6, 0),
                        new L(1, 4), new L(4, 1),
                        new L(3, 5), new L(5, 3),
                        new L(4, 6), new L(6, 4),
                        new L(5, 6), new L(6, 5),
                        new L(7, 6),
                        new L(4, 7), new L(7, 4),
                        new L(6, 3)
                }
        ));

        // 1 -> 7
        assertTrue(matchValidity(eval, 1, 7,
                new L[]{
                        new L(1, 4), new L(4, 7)
                },
                new L[]{
                        new L(0, 1), new L(1, 0),
                        new L(0, 2), new L(2, 0),
                        new L(0, 3), new L(3, 0),
                        new L(0, 6), new L(6, 0),
                        new L(4, 1),
                        new L(3, 5), new L(5, 3),
                        new L(4, 6), new L(6, 4),
                        new L(5, 6), new L(6, 5),
                        new L(6, 7), new L(7, 6),
                        new L(7, 4),
                        new L(3, 6), new L(6, 3)
                }
        ));

        // 1 -> 6
        boolean p1 = matchValidity(eval, 1, 6,
                new L[]{
                        new L(1, 4), new L(4, 6)
                },
                new L[]{
                        new L(0, 1), new L(1, 0),
                        new L(0, 2), new L(2, 0),
                        new L(0, 3), new L(3, 0),
                        new L(0, 6), new L(6, 0),
                        new L(4, 1),
                        new L(3, 5), new L(5, 3),
                        new L(6, 4),
                        new L(5, 6), new L(6, 5),
                        new L(6, 7), new L(7, 6),
                        new L(4, 7), new L(7, 4),
                        new L(3, 6), new L(6, 3)
                }
        );
        boolean p2 = matchValidity(eval, 1, 6,
                new L[]{
                        new L(1, 0), new L(0, 6)
                },
                new L[]{
                        new L(0, 1),
                        new L(0, 2), new L(2, 0),
                        new L(0, 3), new L(3, 0),
                        new L(6, 0),
                        new L(1, 4), new L(4, 1),
                        new L(3, 5), new L(5, 3),
                        new L(4, 6), new L(6, 4),
                        new L(5, 6), new L(6, 5),
                        new L(6, 7), new L(7, 6),
                        new L(4, 7), new L(7, 4),
                        new L(3, 6), new L(6, 3)
                }
        );
        assertTrue((p1 && !p2) || (!p1 && p2));


    }

    @Test
    public void testK2() {

        PathEvaluator eval = PathEvaluatorSelector.select(
                PathEvaluatorSelector.Type.K_SHORTEST_PATHS, g,
                new String[]{ "-ksp", "2", "-peprep", "1"}
        ).getResult();

        // 1 -> 6
        assertTrue(matchValidity(eval, 1, 6,
                new L[]{
                        new L(1, 4), new L(4, 6),
                        new L(1, 0), new L(0, 6)
                },
                new L[]{
                        new L(0, 1),
                        new L(0, 2), new L(2, 0),
                        new L(0, 3), new L(3, 0),
                        new L(6, 0),
                        new L(4, 1),
                        new L(3, 5), new L(5, 3),
                        new L(6, 4),
                        new L(5, 6), new L(6, 5),
                        new L(6, 7), new L(7, 6),
                        new L(4, 7), new L(7, 4),
                        new L(3, 6), new L(6, 3)
                }
        ));

    }

    /*
            new L(0, 1), new L(1, 0),
            new L(0, 2), new L(2, 0),
            new L(0, 3), new L(3, 0),
            new L(6, 0), new L(0, 6),
            new L(1, 4), new L(4, 1),
            new L(3, 5), new L(5, 3),
            new L(4, 6), new L(6, 4),
            new L(5, 6), new L(6, 5),
            new L(6, 7), new L(7, 6),
            new L(4, 7), new L(7, 4),
            new L(3, 6), new L(6, 3)
     */

    // Graph 2
    //
    //   1-4
    //   | |\
    // 2-0-6-7
    //   |/|
    //   3-5

    @Test
    public void testKExtremelyLarge() {

        PathEvaluator eval = PathEvaluatorSelector.select(
                PathEvaluatorSelector.Type.K_SHORTEST_PATHS, g,
                new String[]{ "-ksp", "1000", "-peprep", "1"}
        ).getResult();

        // 1 -> 6
        assertTrue(matchValidity(eval, 1, 6,
                new L[]{
                        new L(1, 0),
                        new L(0, 3),
                        new L(0, 6),
                        new L(1, 4),
                        new L(3, 5),
                        new L(4, 6),
                        new L(5, 6),
                        new L(7, 6),
                        new L(4, 7),
                        new L(3, 6),
                },
                new L[]{
                        new L(0, 1),
                        new L(0, 2), new L(2, 0),
                        new L(3, 0),
                        new L(6, 0),
                        new L(4, 1),
                        new L(5, 3),
                        new L(6, 4),
                        new L(6, 5),
                        new L(6, 7),
                        new L(7, 4),
                        new L(6, 3)
                }
        ));

    }

    private boolean matchValidity(PathEvaluator eval, int src, int dst, L[] inUse, L[] outOfUse) {

        for (L l : inUse) {
            boolean t = !eval.isFlowZero(src, dst, l.i, l.j);
            if (!t) {
                System.out.println("Expected (" + l.i + " -> " + l.j + ") to be in use.");
                return false;
            }
        }

        for (L l : outOfUse) {
            boolean t = eval.isFlowZero(src, dst, l.i, l.j);
            if (!t) {
                System.out.println("Expected (" + l.i + " -> " + l.j + ") to be in use.");
                return false;
            }
        }

        return true;
    }

    private class L {
        int i;
        int j;
        L(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

}
