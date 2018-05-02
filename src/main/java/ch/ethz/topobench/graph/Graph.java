/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph;

import ch.ethz.topobench.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Graph {

    // Utility
    protected static Random rand;
    private static final int INFINITY = 999999999;
    static final int DEFAULT_NUM_HOSTS = 1;   // Default number of hosts associated to each node in the graph

    // Graph-level properties
    private final String name;                   // Name
    protected final int numNodes;                // Number of nodes
    private final int shortestPathLen[][];       // Shortest path between node i and j
    private Vector<Link> adjacencyList[];        // Adjacency list (index is a vector of weighted links to other nodes)
                                                 // It is recommended to only use addBidirNeighbor() and removeBidirNeighbor()
                                                 // as it maintains integrity of the graph as a bi-directional one

    // Node-level properties
    // Note: subclasses can edit this if there are non-uniform weights, but are themselves responsible
    // for the integrity of the weight before each node.
    private final int[] nodeWeight;              // Amount of servers ('hosts') on each node
    private int totalWeight;                     // Total amount of servers ('hosts') in the graph
    private boolean invalidatedWeightMapping;    // Whether the weight mapping needs to be redone next call to svrToSwitch()
    private int[] svrToSwitchMap;                // Map each server to its respective switch
    private int[] switchToFirstSvrMap;           // Map each switch to the first server index

    public Graph(String name, int size) {
        this(name, size, DEFAULT_NUM_HOSTS);
    }

    public Graph(String name, int size, int uniformWeight) {

        // Set as single source of randomness
        rand = Main.universalRand;

        // Direct properties
        this.name = name;
        this.numNodes = size;

        // Allocate all adjacency lists for each node
        allocateAdjacencyList();

        // Node weighing (all 0 in the beginning)
        nodeWeight = new int[numNodes];
        totalWeight = 0;
        invalidatedWeightMapping = false;
        svrToSwitchMap = new int[0];
        switchToFirstSvrMap = new int[0];

        // Setup uniform weight
        if (uniformWeight > 0) {
            setupUniformWeight(uniformWeight);
        }

        // Pre-allocate distance matrix for shortest path length
        this.shortestPathLen = new int[numNodes][numNodes];

    }

    /**
     * Allocate the adjacency list of all nodes.
     */
    @SuppressWarnings("unchecked")
    private void allocateAdjacencyList() {
        adjacencyList = new Vector[numNodes];
        for (int i = 0; i < numNodes; i++) {
            adjacencyList[i] = new Vector<>();
        }
    }

    /**
     * Set the weight (a.k.a. the amount of servers at a node) uniform for all nodes.
     *
     * @param weight    Uniform weight applied to all (must be >= 1)
     */
    protected void setupUniformWeight(int weight) {

        // Set individual weights
        for (int i = 0; i < numNodes; i++) {
            setNodeWeight(i, weight);
        }

    }

    /**
     * Set the weight (a.k.a. the amount of servers at a node) of node i.<br />
     * <br />
     * Automatically invalidates the weight mapping, thus the next call to {@link #svrToSwitch(int) svrToSwitch}
     * will be forced to recalculate the weight mapping (which is expensive).
     *
     * @param i         Node index
     * @param weight    New weight
     */
    protected void setNodeWeight(int i, int weight) {
        int oldWeight = nodeWeight[i];
        totalWeight += weight - oldWeight;
        nodeWeight[i] = weight;
        invalidatedWeightMapping = true;
    }

    /**
     * Retrieve total number of nodes in the graph.
     *
     * @return Total number of nodes
     */
    public int getNumNodes() {
        return numNodes;
    }

    /**
     * Retrieve a list of all the node identifiers that
     * whose node has one or more servers (weight >= 1)
     * attached to it.
     *
     * @return  List of node identifiers
     */
    public List<Integer> getNodesWithWeight() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            if (nodeWeight[i] > 0) {
                result.add(i);
            }
        }
        return result;
    }

    /**
     * Find the switch that belongs to a certain server.
     *
     * @param serverIndex   Server index (determined by Graph)
     *
     * @return Switch (typically: node) index
     */
    public final int svrToSwitch(int serverIndex) {

        // Ensure a valid weight mapping
        ensureValidWeightMapping();

        // Return from mapping
        return svrToSwitchMap[serverIndex];

    }

    /**
     * Find the list of server ids attached to the switch (node).
     *
     * @param nodeIndex     Node index
     *
     * @return  List of server ids
     */
    public final List<Integer> switchToSvrs(int nodeIndex) {

        // Ensure a valid weight mapping
        ensureValidWeightMapping();

        // Add each to the result
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < nodeWeight[nodeIndex]; i++) {
            result.add(switchToFirstSvrMap[nodeIndex] + i);
        }
        return result;

    }

    /**
     * Recalculates the weight mapping if it has
     * been invalidated by a weight change.
     */
    private void ensureValidWeightMapping() {

        // Recalculate weight mapping
        if (invalidatedWeightMapping) {

            // Initialize new mapping
            svrToSwitchMap = new int[totalWeight];
            switchToFirstSvrMap = new int[numNodes];

            // Fill in mapping
            int currentNode = 0;
            int weightBefore = 0;
            for (int s = 0; s < totalWeight; s++) {
                while (s + 1 > weightBefore + nodeWeight[currentNode]) {
                    weightBefore += nodeWeight[currentNode];
                    currentNode++;
                    switchToFirstSvrMap[currentNode] = s;
                }
                svrToSwitchMap[s] = currentNode;
            }

            // Mapping is now correct
            invalidatedWeightMapping = false;

        }

    }

    /**
     * Add a directed edge between the first node and the second node with capacity 1.
     *
     * @param n1 First node index
     * @param n2 Second node index
     * @return True iff successfully added (RunTimeException is thrown if invalid indices are given or n1 = n2)
     */
    private boolean addNeighbor(int n1, int n2) {
        return addNeighbor(n1, n2, 1);
    }

    /**
     * Add a directed edge between the first node and the second node with capacity *cap*.
     * Parallel edges are allowed, thus increasing the capacity of the already existing edge with *cap*.
     *
     * @param n1  First node index
     * @param n2  Second node index
     * @param cap Capacity of link
     * @return True iff successfully added (RunTimeException is thrown if invalid indices are given or n1 = n2)
     */
    private boolean addNeighbor(int n1, int n2, int cap) {

        if (n1 == n2) {
            throw new RuntimeException("ERROR: Graph: addNeighbor: trying to add link to itself for node " + n1 + ".");
        }

        if (n1 >= numNodes || n2 >= numNodes) {
            throw new RuntimeException("ERROR: Graph: addNeighbor: adding link to non-existing node, between node " + n1 + " and node " + n2 + ".");
        }

        // Attempt to find existing link, add capacity of link by *cap* units, thus
        // creating a parallel link.
        for (int i = 0; i < adjacencyList[n1].size(); i++) {
            if (adjacencyList[n1].get(i).getLinkTo() == n2) {
                adjacencyList[n1].elementAt(i).increaseLinkCapacity(cap);
                return true;
            }
        }

        // If not yet an existing link, create a new one
        adjacencyList[n1].add(new Link(n2, cap));

        return true;
    }

    /**
     * Check whether n1 and n2 have edges towards each other and are not equal.
     *
     * @param n1    First node index
     * @param n2    Second node index
     * @return True iff both are bi-directional neighbors and not itself.
     */
    protected boolean isBidirNeighbor(int n1, int n2) {
        return findNeighborIdx(n1, n2) != -1 && findNeighborIdx(n2, n1) != -1;
    }

    /**
     * Find the index in the adjacency list of n1 where n2 is set.
     *
     * @param n1    First node index
     * @param n2    Second node index
     *
     * @return      Index in the adjacency list, -1 if not found
     */
    private int findNeighborIdx(int n1, int n2) {
        for (int i = 0; i < adjacencyList[n1].size(); i++) {
            if (adjacencyList[n1].elementAt(i).getLinkTo() == n2) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Add a bidirectional neighbor both with capacity *cap* either way.
     *
     * @param n1 First node index
     * @param n2 Second node index
     * @return True iff successfully added to graph
     */
    protected boolean addBidirNeighbor(int n1, int n2) {
        return addNeighbor(n1, n2) && addNeighbor(n2, n1);
    }

    /**
     * Add a bidirectional neighbor both with capacity *cap* either way.
     *
     * @param n1  First node index
     * @param n2  Second node index
     * @param cap Capacity of both ways
     * @return True iff successfully added to graph
     */
    protected boolean addBidirNeighbor(int n1, int n2, int cap) {
        return addNeighbor(n1, n2, cap) && addNeighbor(n2, n1, cap);
    }

    /**
     * Remove the given capacity on a bi-directional neighbor.
     *
     * @param n1        First node index
     * @param n2        Second node index
     * @param cap       Capacity to be removed
     *
     * @return     True iff the capacity has been removed between the two.
     */
    protected boolean removeBidirNeighbor(int n1, int n2, int cap) {

        // Find indices in the adjacency list
        int idx1to2 = findNeighborIdx(n1, n2);
        int idx2to1 = findNeighborIdx(n2, n1);

        // Make sure they are both there
        if (idx1to2 == -1 || idx2to1 == -1) {
            throw new RuntimeException("Graph: RemoveBidirNeighbor(1): bi-directional link does not exist.");
        }

        // Remove link from node 1 to node 2
        Link link1to2 = adjacencyList[n1].elementAt(idx1to2);
        if (link1to2.getLinkCapacity() > cap) {
            link1to2.decreaseLinkCapacity(cap);
        } else {
            adjacencyList[n1].remove(idx1to2);
        }

        // Remove link from node 2 to node 1
        Link link2to1 = adjacencyList[n2].elementAt(idx2to1);
        if (link2to1.getLinkCapacity() > cap) {
            link2to1.decreaseLinkCapacity(cap);
        } else {
            adjacencyList[n2].remove(idx2to1);
        }

        return true;

    }

    /**
     * Complete remove the bi-directional link regardless of how much capacity there is.
     *
     * @param n1    First node index
     * @param n2    Second node index
     *
     * @return      True iff both way links are removed.
     */
    protected boolean removeBidirNeighbor(int n1, int n2) {

        // Find indices in the adjacency list
        int idx1to2 = findNeighborIdx(n1, n2);
        int idx2to1 = findNeighborIdx(n2, n1);

        // Make sure they are both there
        if (idx1to2 == -1 || idx2to1 == -1) {
            throw new RuntimeException("Graph: RemoveBidirNeighbor(2): bi-directional link does not exist.");
        }

        // Remove both ways
        adjacencyList[n1].remove(idx1to2);
        adjacencyList[n2].remove(idx2to1);
        return true;

    }

    /**
     * Create string representation of the graph.
     *
     * Following format is returned for each node n:
     * node_index node_weight [list of links "n2_idx (capacity)" - space separated]
     *
     * For a node with index 6, weight of 3, and a four links (cap): 2 (2), 3 (1), 20 (1), 8 (1)
     * 6 3 2 (2) 3 (1) 20 (1) 8 (1)
     *
     * @return Formatted string
     */
    public String toString() {
        String s = "Graph '" + this.name + "':\n";
        for (int i = 0; i < numNodes; i++) {
            s += i + " " + nodeWeight[i];
            for (int j = 0; j < adjacencyList[i].size(); j++) {
                s += " " + adjacencyList[i].elementAt(j).getLinkTo() + " (" + adjacencyList[i].elementAt(j).getLinkCapacity() + ")";
            }
            if (i != numNodes - 1) s += "\n";
        }
        return s;
    }

    /**
     * Calculate all the shortest paths and store them internally.
     * Uses the modified Floyd-Warshall algorithm.
     */
    public void calculateShortestPaths() {

        // Initial scan to find easy shortest paths
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                if (i == j) {
                    shortestPathLen[i][j] = 0;              // To itself
                } else if (findNeighborIdx(i, j) != -1) {
                    shortestPathLen[i][j] = 1;              // To direct neighbor
                } else {
                    shortestPathLen[i][j] = INFINITY;       // To someone not directly connected
                }
            }
        }

        // Floyd-Warshall algorithm
        for (int k = 0; k < numNodes; k++) {
            for (int i = 0; i < numNodes; i++) {
                for (int j = 0; j < numNodes; j++) {
                    if (shortestPathLen[i][j] > shortestPathLen[i][k] + shortestPathLen[k][j]) {
                        shortestPathLen[i][j] = shortestPathLen[i][k] + shortestPathLen[k][j];
                    }
                }
            }
        }

    }

    /**
     * Get the total weight (amount of servers/hosts) in the graph.
     *
     * @return  Total weight
     */
    public int getTotalWeight() {
        return totalWeight;
    }

    /**
     * Get the shortest path length array.
     *
     * Be sure to call {@link #calculateShortestPaths() calculateShortestPaths()}
     * before so that it is not just an array of zeros.
     *
     * @return Shortest path length array
     */
    public int[][] getShortestPathLen() {
        return shortestPathLen;
    }

    /**
     * Get the adjacency list generated.
     *
     * Do *not* edit the array, this function is only there for easily
     * access for e.g. printers. Graph integrity is maintained by the Graph subclass.
     *
     * @return Adjacency list for all nodes
     */
    public Vector<Link>[] getAdjacencyList() {
        return adjacencyList;
    }

    /**
     * Get all the node weight of node i.
     *
     * @return Node weight ('number of servers')
     */
    public int getNodeWeight(int i) {
        return nodeWeight[i];
    }

    /**
     * Compute the amount of bi-directional edges, assuming that
     * all edges added are bidirectional.
     *
     * @return Number of bi-directional edges
     */
    public int getNumberBidirEdges() {
        int totalEdges = 0;
        for (int i = 0; i < numNodes; i++) {
            totalEdges += adjacencyList[i].size();
        }
        return totalEdges / 2;
    }

    /**
     * Fail a percentage of all links in the graph.
     * This function assumes it is a bi-directional graph.
     *
     * @param percentage    Percentage of how many to fail
     */
    public void failLinks(double percentage) {

        if (percentage < 0.0 || percentage > 1.0) {
            throw new RuntimeException("Graph: failLinks: fail percentage must be between 0.0 and 1.0 (given: " + percentage + ")");
        }

        // First find the total number of bi-directional edges
        int totalBidirEdges = getNumberBidirEdges();
        int totalEdges = totalBidirEdges * 2;

        // Fail links one-by-one
        int numFail = (int) Math.round(percentage * totalBidirEdges);

        int failedUntilNow = 0;
        while (failedUntilNow < numFail) {

            // Determine index to fail
            int linkToFail = rand.nextInt(totalEdges);

            // Find and remove link both ways
            int c = 0;
            for (int i = 0; i < numNodes; i++) {
                for (int j = 0; j < adjacencyList[i].size(); j++) {
                    if (c == linkToFail) {
                        removeBidirNeighbor(i, adjacencyList[i].get(j).getLinkTo());
                    }
                    c++;
                }
            }

            // Update statistics
            failedUntilNow++;
            totalEdges -= 2;

        }

    }

}
