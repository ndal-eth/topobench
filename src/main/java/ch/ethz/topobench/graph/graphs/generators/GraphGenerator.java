/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.graphs.generators;

import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.Graph;

interface GraphGenerator {
    SelectorResult<Graph> generate(String[] args);
}
