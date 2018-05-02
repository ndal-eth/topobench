/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.patheval;

import ch.ethz.topobench.graph.Graph;

public class SlackPathEvaluator extends PathEvaluator {

    private int slack;

    public SlackPathEvaluator(Graph g, int slack) {
        super(g);
        this.slack = slack;
    }

    /**
     * Do not include flow over links that deviate more than or equal to
     * shortest path length.
     *
     * This means in practice:
     * slack=0 - Only shortest paths are used
     * slack=1 - Only shortest paths and shortest paths + 1 are used
     * ...
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
        return
                shortestPathLen[src][linkFrom] + 1 + shortestPathLen[linkTo][dst]
                        >
                shortestPathLen[src][dst] + this.slack
        ;
    }

}
