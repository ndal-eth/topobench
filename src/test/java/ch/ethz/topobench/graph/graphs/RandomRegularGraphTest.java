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
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class RandomRegularGraphTest {

    @Test
    public void testConstructions() {
        testConstruction(6, 1, 180);
        testConstruction(9, 2, 90);
        testConstruction(18, 1, 456);
        testConstruction(20, 4, 777);
        testConstruction(8, 12, 889);
        testConstruction(17, 1, 5737);
        // Takes too long: testConstruction(56, 3, 9986);
    }

    public void testConstruction(int d, int s, int n) {

        // Create graph and expectation
        Main.loadRunEnvironmentConfiguration();
        Main.initGlobalRandomness(8395839);

        // Create graph
        RandomRegularGraph graph = (RandomRegularGraph) GraphSelector.select(
                GraphSelector.Type.RANDOM_REGULAR_GRAPH,
                new String[]{
                        "-switches", String.valueOf(n),
                        "-switchports", String.valueOf(d + s),
                        "-netports", String.valueOf(d)
                }
        ).getResult();

        // Base statistics should match
        assertEquals(n, graph.getNumNodes());

        // Confirm that all switches have less than or equal to d edges, with all capacity 1 and not to themselves
        for (int i = 0; i < n; i++) {
            int actualD = graph.getAdjacencyList()[i].size();
            assertTrue(actualD <= d);
            for (int j = 0; j < actualD; j++) {
                assertEquals(graph.getAdjacencyList()[i].get(j).getLinkCapacity(), 1);
                assertThat(graph.getAdjacencyList()[i].get(j).getLinkTo(), not(i));
            }
        }

        // Weight checking
        assertEquals(n * s, graph.getTotalWeight());
        for (int i = 0; i < n ; i++) {
            assertEquals(s, graph.getNodeWeight(i));
        }
        for (int i = 0; i < n * s; i++) {
            assertEquals((int) Math.floor(i / s), graph.svrToSwitch(i));
        }

    }

}
