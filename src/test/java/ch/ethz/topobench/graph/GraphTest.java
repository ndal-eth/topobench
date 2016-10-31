/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph;

import ch.ethz.topobench.Main;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GraphTest {

    @Test
    public void testInitialStates() {
        testInitialState(3456, 1);
        testInitialState(3456, 2);
        testInitialState(4, 3);
        testInitialState(4, -1);

    }

    public void testInitialState(int n, int wUnif) {

        // Create graph
        Graph g;
        if (wUnif == -1) {
            wUnif = Graph.DEFAULT_NUM_HOSTS;
            g = new Graph("test", n);
        } else {
            g = new Graph("test", n, wUnif);
        }

        // Nodes
        assertEquals(g.getNumNodes(), n);
        assertEquals(g.getTotalWeight(), n * wUnif);

        // Edges
        assertEquals(g.getAdjacencyList().length, n);
        for (int i = 0; i < n; i++) {
            assertEquals(g.getAdjacencyList()[i].size(), 0);
        }

    }

    @Test
    public void testFailLinksMultiple() {
        for (int x = 0; x <= 100; x += 1) {
            testFailLinks(x);
        }
    }

    public void testFailLinks(int x) {

        // Create graph
        Main.initGlobalRandomness(0);
        Graph g = new Graph("test", 101);

        // Add 100 bi-directional edges
        for (int i = 0; i < 100; i++) {
            g.addBidirNeighbor(i, i + 1);
        }

        // Fail percentage of links
        g.failLinks(x / 100.0);

        // Assume that x% has failed
        assertEquals(100 - x, g.getNumberBidirEdges());

    }

    @Test
    public void testFailLinksException() {

        // Create graph
        Main.initGlobalRandomness(0);
        Graph g = new Graph("test", 101);

        // Negative percentage
        boolean t = false;
        try {
            g.failLinks(-0.1);
        } catch (RuntimeException e) {
            t = true;
        }
        assertTrue(t);

        // Impossibly large percentage
        t = false;
        try {
            g.failLinks(1.1);
        } catch (RuntimeException e) {
            t = true;
        }
        assertTrue(t);

    }

    @Test
    public void testBidirNeighborsStar() {
        Graph g = new Graph("test", 5);

        // Make start topology
        g.addBidirNeighbor(0, 1);
        g.addBidirNeighbor(0, 2);
        g.addBidirNeighbor(0, 3);
        g.addBidirNeighbor(0, 4);

        // Center node
        assertEquals(g.getAdjacencyList()[0].size(), 4);
        assertEquals(g.getAdjacencyList()[0].get(0).getLinkTo(), 1);
        assertEquals(g.getAdjacencyList()[0].get(1).getLinkTo(), 2);
        assertEquals(g.getAdjacencyList()[0].get(2).getLinkTo(), 3);
        assertEquals(g.getAdjacencyList()[0].get(3).getLinkTo(), 4);

        // Outer nodes
        assertEquals(g.getAdjacencyList()[1].size(), 1);
        assertEquals(g.getAdjacencyList()[1].get(0).getLinkTo(), 0);
        assertEquals(g.getAdjacencyList()[2].size(), 1);
        assertEquals(g.getAdjacencyList()[2].get(0).getLinkTo(), 0);
        assertEquals(g.getAdjacencyList()[3].size(), 1);
        assertEquals(g.getAdjacencyList()[3].get(0).getLinkTo(), 0);
        assertEquals(g.getAdjacencyList()[4].size(), 1);
        assertEquals(g.getAdjacencyList()[4].get(0).getLinkTo(), 0);

    }

    @Test
    public void testAddBidirNeighborsLargeLinksTriangle() {

        // Construct graph
        Graph g = new Graph("test", 3);
        g.addBidirNeighbor(0, 1);
        g.addBidirNeighbor(1, 2);
        g.addBidirNeighbor(2, 1);
        g.addBidirNeighbor(2, 0);
        g.addBidirNeighbor(0, 2);
        g.addBidirNeighbor(0, 2);

        // Test degrees
        assertEquals(g.getAdjacencyList()[0].size(), 2);
        assertEquals(g.getAdjacencyList()[1].size(), 2);
        assertEquals(g.getAdjacencyList()[2].size(), 2);

        // Test to and capacity for each node
        assertEquals(g.getAdjacencyList()[0].get(0).getLinkTo(), 1);
        assertEquals(g.getAdjacencyList()[0].get(0).getLinkCapacity(), 1);
        assertEquals(g.getAdjacencyList()[0].get(1).getLinkTo(), 2);
        assertEquals(g.getAdjacencyList()[0].get(1).getLinkCapacity(), 3);

        assertEquals(g.getAdjacencyList()[1].get(0).getLinkTo(), 0);
        assertEquals(g.getAdjacencyList()[1].get(0).getLinkCapacity(), 1);
        assertEquals(g.getAdjacencyList()[1].get(1).getLinkTo(), 2);
        assertEquals(g.getAdjacencyList()[1].get(1).getLinkCapacity(), 2);

        assertEquals(g.getAdjacencyList()[2].get(0).getLinkTo(), 1);
        assertEquals(g.getAdjacencyList()[2].get(0).getLinkCapacity(), 2);
        assertEquals(g.getAdjacencyList()[2].get(1).getLinkTo(), 0);
        assertEquals(g.getAdjacencyList()[2].get(1).getLinkCapacity(), 3);

        // Remove all except single edge
        g.removeBidirNeighbor(0, 1);
        g.removeBidirNeighbor(1, 2, 1);
        g.removeBidirNeighbor(0, 2, 3);

        // Check that only edge with flow 1 remains
        assertEquals(g.getNumberBidirEdges(), 1);
        assertEquals(g.getAdjacencyList()[0].size(), 0);
        assertEquals(g.getAdjacencyList()[1].size(), 1);
        assertEquals(g.getAdjacencyList()[2].size(), 1);
        assertEquals(g.getAdjacencyList()[1].get(0).getLinkCapacity(), 1);
        assertEquals(g.getAdjacencyList()[1].get(0).getLinkTo(), 2);
        assertEquals(g.getAdjacencyList()[2].get(0).getLinkCapacity(), 1);
        assertEquals(g.getAdjacencyList()[2].get(0).getLinkTo(), 1);

    }

    @Test
    public void testCalculateShortestPathsTriangle() {

        // Construct triangle graph
        Graph g = new Graph("test", 3);
        g.addBidirNeighbor(0, 1);
        g.addBidirNeighbor(1, 2);
        g.addBidirNeighbor(2, 1);
        g.addBidirNeighbor(2, 0);
        g.addBidirNeighbor(0, 2);
        g.addBidirNeighbor(0, 2);

        g.calculateShortestPaths();

        int[][] shortestPathLen = g.getShortestPathLen();
        assertEquals(shortestPathLen[0][0], 0);
        assertEquals(shortestPathLen[0][1], 1);
        assertEquals(shortestPathLen[0][2], 1);
        assertEquals(shortestPathLen[1][0], 1);
        assertEquals(shortestPathLen[1][1], 0);
        assertEquals(shortestPathLen[1][2], 1);
        assertEquals(shortestPathLen[2][0], 1);
        assertEquals(shortestPathLen[2][1], 1);
        assertEquals(shortestPathLen[2][2], 0);

    }

    @Test
    public void testCalculateShortestPathsLinkedList() {

        // Construct triangle graph
        Graph g = new Graph("test", 5);
        g.addBidirNeighbor(0, 1);
        g.addBidirNeighbor(1, 2);
        g.addBidirNeighbor(2, 3);
        g.addBidirNeighbor(3, 4);

        g.calculateShortestPaths();

        int[][] shortestPathLen = g.getShortestPathLen();
        assertEquals(shortestPathLen[0][0], 0);
        assertEquals(shortestPathLen[0][1], 1);
        assertEquals(shortestPathLen[0][2], 2);
        assertEquals(shortestPathLen[0][3], 3);
        assertEquals(shortestPathLen[0][4], 4);

        assertEquals(shortestPathLen[1][0], 1);
        assertEquals(shortestPathLen[1][1], 0);
        assertEquals(shortestPathLen[1][2], 1);
        assertEquals(shortestPathLen[1][3], 2);
        assertEquals(shortestPathLen[1][4], 3);

        assertEquals(shortestPathLen[2][0], 2);
        assertEquals(shortestPathLen[2][1], 1);
        assertEquals(shortestPathLen[2][2], 0);
        assertEquals(shortestPathLen[2][3], 1);
        assertEquals(shortestPathLen[2][4], 2);

        assertEquals(shortestPathLen[3][0], 3);
        assertEquals(shortestPathLen[3][1], 2);
        assertEquals(shortestPathLen[3][2], 1);
        assertEquals(shortestPathLen[3][3], 0);
        assertEquals(shortestPathLen[3][4], 1);

        assertEquals(shortestPathLen[4][0], 4);
        assertEquals(shortestPathLen[4][1], 3);
        assertEquals(shortestPathLen[4][2], 2);
        assertEquals(shortestPathLen[4][3], 1);
        assertEquals(shortestPathLen[4][4], 0);

    }

    @Test
    public void testAddbidirNeighborLinkedList() {
        Graph g = new Graph("test", 100);

        // Add 99 bi-directional edges
        for (int i = 0; i < 99; i++) {
            g.addBidirNeighbor(i, i + 1);
        }

        assertEquals(99, g.getNumberBidirEdges());

        // Starting node
        assertEquals(g.getAdjacencyList()[0].size(), 1);
        assertEquals(g.getAdjacencyList()[0].get(0).getLinkCapacity(), 1);
        assertEquals(g.getAdjacencyList()[0].get(0).getLinkTo(), 1);

        // Nodes in between
        for (int i = 1; i < 99; i++) {
            assertEquals(g.getAdjacencyList()[i].size(), 2);
            assertEquals(g.getAdjacencyList()[i].get(0).getLinkCapacity(), 1);
            assertEquals(g.getAdjacencyList()[i].get(0).getLinkTo(), i - 1);
            assertEquals(g.getAdjacencyList()[i].get(1).getLinkCapacity(), 1);
            assertEquals(g.getAdjacencyList()[i].get(1).getLinkTo(), i + 1);

        }

        // Ending node
        assertEquals(g.getAdjacencyList()[99].size(), 1);
        assertEquals(g.getAdjacencyList()[99].get(0).getLinkCapacity(), 1);
        assertEquals(g.getAdjacencyList()[99].get(0).getLinkTo(), 98);

    }

    @Test
    public void testSvrToSwitchUniform() {
        Graph g = new Graph("test", 100, 2);
        assertEquals(200, g.getTotalWeight());
        for (int s = 0; s < 200; s++) {
            assertEquals((int) Math.floor(s / 2), g.svrToSwitch(s));
        }
    }

    @Test
    public void testSvrToSwitchSkewed() {
        Graph g = new Graph("test", 10, 0);
        assertEquals(0, g.getTotalWeight());

        g.setNodeWeight(0, 10);
        g.setNodeWeight(2, 17);
        g.setNodeWeight(4, 3);
        g.setNodeWeight(6, 7);
        g.setNodeWeight(7, 88);

        assertEquals(g.getTotalWeight(), 125);

        for (int s = 0; s < 10; s++) {
            assertEquals(0, g.svrToSwitch(s));
        }

        for (int s = 10; s < 27; s++) {
            assertEquals(2, g.svrToSwitch(s));
        }

        for (int s = 27; s < 30; s++) {
            assertEquals(4, g.svrToSwitch(s));
        }

        for (int s = 30; s < 37; s++) {
            assertEquals(6, g.svrToSwitch(s));
        }

        for (int s = 37; s < 125; s++) {
            assertEquals(7, g.svrToSwitch(s));
        }

    }

    @Test
    public void testSvrToSwitchSingle() {

        Graph g = new Graph("test", 10, 0);
        assertEquals(0, g.getTotalWeight());
        g.setNodeWeight(7, 1);

        // Total weight
        assertEquals(g.getTotalWeight(), 1);

        // Mapping
        assertEquals(7, g.svrToSwitch(0));

        // Individual node weights
        assertEquals(0, g.getNodeWeight(0));
        assertEquals(0, g.getNodeWeight(1));
        assertEquals(0, g.getNodeWeight(2));
        assertEquals(0, g.getNodeWeight(3));
        assertEquals(0, g.getNodeWeight(4));
        assertEquals(0, g.getNodeWeight(5));
        assertEquals(0, g.getNodeWeight(6));
        assertEquals(1, g.getNodeWeight(7));
        assertEquals(0, g.getNodeWeight(8));
        assertEquals(0, g.getNodeWeight(9));

    }

    @Test
    public void testSwitchToSvrsUniform() {
        Graph g = new Graph("test", 100, 2);
        for (int i = 0; i < 100; i++) {
            assertTrue(validate(i * 2, i * 2 + 2, g.switchToSvrs(i)));
        }
        List<Integer> list = g.getNodesWithWeight();
        for (int i = 0; i < 100; i++) {
            assertTrue(list.contains(i));
        }
        assertFalse(list.contains(100));
    }

    @Test
    public void testSwitchToSvrsSkewed() {
        Graph g = new Graph("test", 10, 0);
        assertEquals(0, g.getTotalWeight());

        g.setNodeWeight(0, 10);
        g.setNodeWeight(2, 17);
        g.setNodeWeight(4, 3);
        g.setNodeWeight(6, 7);
        g.setNodeWeight(7, 88);

        assertTrue(validate(0, 10, g.switchToSvrs(0)));
        assertTrue(validate(10, 10, g.switchToSvrs(1)));
        assertTrue(validate(10, 27, g.switchToSvrs(2)));
        assertTrue(validate(27, 27, g.switchToSvrs(3)));
        assertTrue(validate(27, 30, g.switchToSvrs(4)));
        assertTrue(validate(30, 30, g.switchToSvrs(5)));
        assertTrue(validate(30, 37, g.switchToSvrs(6)));
        assertTrue(validate(37, 125, g.switchToSvrs(7)));
        assertTrue(validate(125, 125, g.switchToSvrs(8)));
        assertTrue(validate(125, 125, g.switchToSvrs(9)));

        List<Integer> list = g.getNodesWithWeight();
        assertTrue(list.contains(0));
        assertTrue(list.contains(2));
        assertTrue(list.contains(4));
        assertTrue(list.contains(6));
        assertTrue(list.contains(7));
        assertFalse(list.contains(1));
        assertFalse(list.contains(3));
        assertFalse(list.contains(5));
        assertFalse(list.contains(8));
        assertFalse(list.contains(9));
        assertFalse(list.contains(10));

    }

    private boolean validate(int start, int end, List<Integer> actual) {

        for (int i = start; i < end; i++) {
            if (!actual.contains(i)) {
                return false;
            }
        }

        return (end - start) == actual.size();

    }

}
