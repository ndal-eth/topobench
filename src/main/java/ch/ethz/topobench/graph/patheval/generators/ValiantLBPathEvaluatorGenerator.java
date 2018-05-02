/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.patheval.generators;

import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.patheval.ValiantLBPathEvaluator;
import ch.ethz.topobench.graph.utility.ArgumentValidator;
import ch.ethz.topobench.graph.utility.CmdAssistant;
import ch.ethz.topobench.graph.patheval.PathEvaluator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class ValiantLBPathEvaluatorGenerator implements PathEvaluatorGenerator {

    @Override
    public SelectorResult<PathEvaluator> generate(Graph graph, String[] args) {

        // Parse the options
        Options options = new Options();
        CmdAssistant.addOption(options, "kvlb", "k for k-valiant load balancing");
        CommandLine cmd = CmdAssistant.parseOptions(options, args, true);

        // Read in parameters
        int kvlb = ArgumentValidator.retrieveInteger("kvlb", cmd.getOptionValue("kvlb"));

        // Create path evaluator
        return new SelectorResult<>(new ValiantLBPathEvaluator(graph, kvlb), cmd.getArgs());

    }

}
