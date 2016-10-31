/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.patheval;

import ch.ethz.topobench.Main;
import ch.ethz.topobench.graph.TestGraph;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ValiantLBPathEvaluatorTest {

    private TestGraph g;

    @Before
    public void setup() {

        // Graph
        //
        //   1
        //  /|\
        // 0-2-4
        //  \|/
        //   3

        g = new TestGraph("test", 5);
        g.addBidirNeighbor(0, 1);
        g.addBidirNeighbor(0, 2);
        g.addBidirNeighbor(0, 3);
        g.addBidirNeighbor(1, 4);
        g.addBidirNeighbor(2, 4);
        g.addBidirNeighbor(3, 4);
        g.addBidirNeighbor(2, 1);
        g.addBidirNeighbor(2, 3);

    }

    @Test
    public void testK1() {

        for (int seed = 24; seed < 388353; seed += 839) {

            Main.initGlobalRandomness(seed);
            PathEvaluator eval = PathEvaluatorSelector.select(
                    PathEvaluatorSelector.Type.VALIANT, g,
                    new String[]{"-kvlb", "1"}
            ).getResult();

            // 0 -> 4, three possibilities with k=1
            boolean p1 = matchValidity(eval, 0, 4,
                    new L[]{
                            new L(0, 1), new L(1, 4)
                    },
                    new L[]{
                            new L(1, 0),
                            new L(0, 2), new L(2, 0),
                            new L(0, 3), new L(3, 0),
                            new L(4, 1),
                            new L(2, 4), new L(4, 2),
                            new L(3, 4), new L(4, 3),
                            new L(1, 2), new L(2, 1),
                            new L(2, 3), new L(3, 2),
                    }
            );
            boolean p2 = matchValidity(eval, 0, 4,
                    new L[]{
                            new L(0, 2), new L(2, 4)
                    },
                    new L[]{
                            new L(0, 1), new L(1, 0),
                            new L(2, 0),
                            new L(0, 3), new L(3, 0),
                            new L(1, 4), new L(4, 1),
                            new L(4, 2),
                            new L(3, 4), new L(4, 3),
                            new L(1, 2), new L(2, 1),
                            new L(2, 3), new L(3, 2),
                    }
            );
            boolean p3 = matchValidity(eval, 0, 4,
                    new L[]{
                            new L(0, 3), new L(3, 4)
                    },
                    new L[]{
                            new L(0, 1), new L(1, 0),
                            new L(0, 2), new L(2, 0),
                            new L(3, 0),
                            new L(1, 4), new L(4, 1),
                            new L(2, 4), new L(4, 2),
                            new L(4, 3),
                            new L(1, 2), new L(2, 1),
                            new L(2, 3), new L(3, 2),
                    }
            );
            assertTrue((p1 && !p2 && !p3) || (!p1 && p2 && !p3) || (!p1 && !p2 && p3));

        }

    }

    @Test
    public void testK2() {

        for (int seed = 24; seed < 388353; seed += 839) {

            Main.initGlobalRandomness(seed);
            PathEvaluator eval = PathEvaluatorSelector.select(
                    PathEvaluatorSelector.Type.VALIANT, g,
                    new String[]{"-kvlb", "2"}
            ).getResult();

            // 0 -> 4, three possibilities with k=2
            boolean p1 = matchValidity(eval, 0, 4,
                    new L[]{
                            new L(0, 1), new L(1, 4),
                            new L(0, 2), new L(2, 4)
                    },
                    new L[]{
                            new L(1, 0),
                            new L(2, 0),
                            new L(0, 3), new L(3, 0),
                            new L(4, 1),
                            new L(4, 2),
                            new L(3, 4), new L(4, 3),
                            new L(1, 2), new L(2, 1),
                            new L(2, 3), new L(3, 2),
                    }
            );
            boolean p2 = matchValidity(eval, 0, 4,
                    new L[]{
                            new L(0, 2), new L(2, 4),
                            new L(0, 3), new L(3, 4)
                    },
                    new L[]{
                            new L(0, 1), new L(1, 0),
                            new L(2, 0),
                            new L(3, 0),
                            new L(1, 4), new L(4, 1),
                            new L(4, 2),
                            new L(4, 3),
                            new L(1, 2), new L(2, 1),
                            new L(2, 3), new L(3, 2),
                    }
            );
            boolean p3 = matchValidity(eval, 0, 4,
                    new L[]{
                            new L(0, 1), new L(1, 4),
                            new L(0, 3), new L(3, 4)
                    },
                    new L[]{
                            new L(1, 0),
                            new L(0, 2), new L(2, 0),
                            new L(3, 0),
                            new L(4, 1),
                            new L(2, 4), new L(4, 2),
                            new L(4, 3),
                            new L(1, 2), new L(2, 1),
                            new L(2, 3), new L(3, 2),
                    }
            );
            assertTrue((p1 && !p2 && !p3) || (!p1 && p2 && !p3) || (!p1 && !p2 && p3));

        }

    }

    @Test
    public void testK3() {

        for (int seed = 24; seed < 388353; seed += 839) {

            Main.initGlobalRandomness(seed);
            PathEvaluator eval = PathEvaluatorSelector.select(
                    PathEvaluatorSelector.Type.VALIANT, g,
                    new String[]{"-kvlb", "3"}
            ).getResult();

            // 0 -> 4, one possibilities with k=3
            assertTrue(matchValidity(eval, 0, 4,
                    new L[]{
                            new L(0, 1), new L(1, 4),
                            new L(0, 2), new L(2, 4),
                            new L(0, 3), new L(3, 4)
                    },
                    new L[]{
                            new L(1, 0),
                            new L(2, 0),
                            new L(3, 0),
                            new L(4, 1),
                            new L(4, 2),
                            new L(4, 3),
                            new L(1, 2), new L(2, 1),
                            new L(2, 3), new L(3, 2),
                    }
            ));

        }

    }

    private boolean matchValidity(PathEvaluator eval, int src, int dst, L[] inUse, L[] outOfUse) {

        for (L l : inUse) {
            boolean t = !eval.isFlowZero(src, dst, l.i, l.j);
            if (!t) {
                // System.out.println("Expected (" + l.i + " -> " + l.j + ") to be in use.");
                return false;
            }
        }

        for (L l : outOfUse) {
            boolean t = eval.isFlowZero(src, dst, l.i, l.j);
            if (!t) {
                // System.out.println("Expected (" + l.i + " -> " + l.j + ") to be in use.");
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
