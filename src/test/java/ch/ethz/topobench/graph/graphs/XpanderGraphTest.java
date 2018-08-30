/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.graphs;

import ch.ethz.topobench.Main;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class XpanderGraphTest {

    @Test
    public void testConstructions() {
        testConstruction(6, 1, 180);
        testConstruction(9, 2, 90);
        testConstruction(18, 1, 456);
        //testConstruction(20, 4, 777);
        //testConstruction(17, 1, 5737);
        // Takes too long: testConstruction(56, 3, 9986);
    }

    public void testConstruction(int d, int s, int desiredN) {

        // Create graph and expectation
        Main.loadRunEnvironmentConfiguration();
        Main.initGlobalRandomness(8395839);

        // Create graph
        XpanderGraph graph = (XpanderGraph) GraphSelector.select(
                GraphSelector.Type.XPANDER,
                new String[]{
                        "-switches", String.valueOf(desiredN),
                        "-switchports", String.valueOf(d + s),
                        "-netports", String.valueOf(d)
                }
        ).getResult();

        // Create expectation
        int expectedK = (int) Math.ceil( ((double) desiredN) / ((double) (d + 1)));
        int expectedN = expectedK * (d + 1);

        // Base statistics should match
        assertEquals(expectedK, graph.getK());
        assertEquals(d, graph.getD());
        assertEquals(s, graph.getS());
        assertEquals(expectedN, graph.getNumNodes());

        // Confirm that all switches have exactly d edges, with all capacity 1 and not to themselves
        for (int i = 0; i < expectedN; i++) {
            assertEquals(graph.getAdjacencyList()[i].size(), d);
            for (int j = 0; j < d; j++) {
                assertEquals(graph.getAdjacencyList()[i].get(j).getLinkCapacity(), 1);
                assertThat(graph.getAdjacencyList()[i].get(j).getLinkTo(), not(i));
            }
        }

        // Weight checking
        assertEquals(expectedN * s, graph.getTotalWeight());
        for (int i = 0; i < expectedN ; i++) {
            assertEquals(s, graph.getNodeWeight(i));
        }
        for (int i = 0; i < expectedN * s; i++) {
            assertEquals((int) Math.floor(i / s), graph.svrToSwitch(i));
        }

    }

}
