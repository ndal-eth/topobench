/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.traffic;

import ch.ethz.topobench.graph.TestGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TrafficTest {

    @Test
    public void testIllegalArgumentWithNoWeightedNodes() {

        TestGraph g1 = new TestGraph("test", 2);
        g1.setNodeWeight(0, 0);
        g1.setNodeWeight(1, 0);

        boolean thrown = false;
        try {
            new AllToAllTraffic(g1);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

    }

    @Test
    public void testEnsureNodesWithWeightAreUniform() {

        // Non-uniform weight
        TestGraph g1 = new TestGraph("test", 2);
        g1.setNodeWeight(0, 1);
        g1.setNodeWeight(1, 2);
        boolean thrown = false;
        try {
            new StrideTraffic(g1, 10);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

        // No exception should be thrown
        TestGraph g2 = new TestGraph("test", 3);
        g2.setNodeWeight(0, 1);
        g2.setNodeWeight(1, 1);
        g2.setNodeWeight(2, 0);
        new StrideTraffic(g2, 1);

        // No exception should be thrown
        TestGraph g3 = new TestGraph("test", 5);
        g3.setNodeWeight(0, 0);
        g3.setNodeWeight(1, 2);
        g3.setNodeWeight(2, 2);
        g3.setNodeWeight(3, 2);
        g3.setNodeWeight(4, 2);
        new StrideTraffic(g3, 3);

    }

}
