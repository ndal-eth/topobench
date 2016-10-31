/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.patheval;

import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.Link;

import java.util.Vector;

public abstract class PathEvaluator {

    int[][] shortestPathLen;
    Vector<Link> adjacencyList[];

    PathEvaluator(Graph graph) {
        this.shortestPathLen = graph.getShortestPathLen();
        this.adjacencyList = graph.getAdjacencyList();
    }

    /**
     * Determine whether a particular link (i, j) should NOT be included in the
     * optimization of flow from src to dst.
     *
     * @param src           Source of flow
     * @param dst           Destination of flow
     * @param linkFrom      i-component of link
     * @param linkTo        j-component of link
     *
     * @return  False iff the link (i, j) should be considered for flow src -> dst
     */
    public abstract boolean isFlowZero(int src, int dst, int linkFrom, int linkTo);

}
