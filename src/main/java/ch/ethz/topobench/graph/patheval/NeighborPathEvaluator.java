/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.patheval;

import ch.ethz.topobench.graph.Graph;

public class NeighborPathEvaluator extends PathEvaluator {

    public NeighborPathEvaluator(Graph g) {
        super(g);
    }

    /**
     * Do not include flow over links that are not part of the shortest paths
     * of the neighbors of the origin of the flow to the destination.
     *
     * @param src           Source of flow
     * @param dst           Destination of flow
     * @param linkFrom      i-component of link
     * @param linkTo        j-component of link
     *
     * @return False iff the link (i, j) should be considered for flow src -> dst
     */
    @Override
    public boolean isFlowZero(int src, int dst, int linkFrom, int linkTo) {

        // Check whether it is one of the outgoing edges
        if (src == linkFrom) {
            return false;
        }

        // Check whether the edge is on any other shortest paths of the neighbors
        boolean isOnShortestPath = false;
        for (int i = 0; i < adjacencyList[src].size(); i++) {
            int neighborSrc = adjacencyList[src].get(i).getLinkTo();
            if (shortestPathLen[neighborSrc][linkFrom] + 1 + shortestPathLen[linkTo][dst] <= shortestPathLen[neighborSrc][dst]) {
                isOnShortestPath = true;
                break;
            }
        }

        // Flow is zero when it is not on any of the shortest paths of the neighbors
        return !isOnShortestPath;

    }

}
