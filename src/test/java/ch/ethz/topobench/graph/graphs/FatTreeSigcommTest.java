/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.graphs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FatTreeSigcommTest {

    @Test
    public void testConstructions() {
        testConstruction(2);
        testConstruction(4);
        testConstruction(6);
        testConstruction(8);
        testConstruction(10);
        testConstruction(18);
        testConstruction(32);
    }

    public void testConstruction(int k) {

        // Create graph and expectation
        FatTreeSigcomm graph = (FatTreeSigcomm) GraphSelector.select(
                GraphSelector.Type.FAT_TREE,
                new String[]{"-kft", String.valueOf(k)}
        ).getResult();

        // Base statistics should match
        assertEquals(k, graph.getK());
        assertEquals(k * k * 5 / 4, graph.getNumNodes());

        // Confirm that all bottom switches have k/2 edges, with all capacity 1 and not to themselves
        for (int i = 0; i < k * k / 2 ; i++) {
            assertEquals(k / 2, graph.getAdjacencyList()[i].size());
            for (int j = 0; j < k / 2; j++) {
                assertEquals(1, graph.getAdjacencyList()[i].get(j).getLinkCapacity());
                assertThat(graph.getAdjacencyList()[i].get(j).getLinkTo(), not(i));
            }
        }

        // Confirm that all middle and top switches have k edges, with all capacity 1 and not to themselves
        for (int i = k * k / 2; i < k * k / 5 * 4; i++) {
            assertEquals(k, graph.getAdjacencyList()[i].size());
            for (int j = 0; j < k; j++) {
                assertEquals(1, graph.getAdjacencyList()[i].get(j).getLinkCapacity());
                assertThat(graph.getAdjacencyList()[i].get(j).getLinkTo(), not(i));
            }
        }

        // Weight checking
        assertEquals(k * k * k / 4, graph.getTotalWeight());
        for (int i = 0; i < k * k / 2 ; i++) {
            assertEquals(k / 2, graph.getNodeWeight(i));
        }
        for (int i = k * k / 2; i < k * k * 5 / 4 ; i++) {
            assertEquals(0, graph.getNodeWeight(i));
        }

    }

}
