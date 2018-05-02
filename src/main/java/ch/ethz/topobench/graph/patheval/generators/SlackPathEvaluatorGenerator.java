/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.patheval.generators;

import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.utility.ArgumentValidator;
import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.patheval.PathEvaluator;
import ch.ethz.topobench.graph.patheval.SlackPathEvaluator;
import ch.ethz.topobench.graph.utility.CmdAssistant;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import static ch.ethz.topobench.graph.utility.CmdAssistant.parseOptions;

public class SlackPathEvaluatorGenerator implements PathEvaluatorGenerator {

    @Override
    public SelectorResult<PathEvaluator> generate(Graph graph, String[] args) {

        // Parse the options
        Options options = new Options();
        CmdAssistant.addOption(options, "slk", "slack", true, "slack (-1 = inf)");
        CommandLine cmd = parseOptions(options, args, true);

        // Read in parameters
        int slack = ArgumentValidator.retrieveInteger("slk", cmd.getOptionValue("slk"));

        // Infinity option
        if (slack == -1) {
            slack = 1000000;
        }

        System.out.println("> Slack (1000000 is practical infinite): " + slack);

        // Create path evaluator
        return new SelectorResult<>(new SlackPathEvaluator(graph, slack), cmd.getArgs());

    }

}
