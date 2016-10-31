/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.patheval.generators;

import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.patheval.NeighborPathEvaluator;
import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.patheval.PathEvaluator;
import ch.ethz.topobench.graph.utility.CmdAssistant;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;


public class NeighborPathEvaluatorGenerator implements PathEvaluatorGenerator {

    @Override
    public SelectorResult<PathEvaluator> generate(Graph graph, String[] args) {

        // Parse the options
        CommandLine cmd = CmdAssistant.parseOptions(new Options(), args, true);

        // Return path evaluator
        return new SelectorResult<>(new NeighborPathEvaluator(graph), cmd.getArgs());

    }

}
