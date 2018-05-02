/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.patheval.generators;

import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.patheval.KShortestPathEvaluator;
import ch.ethz.topobench.graph.utility.ArgumentValidator;
import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.patheval.PathEvaluator;
import ch.ethz.topobench.graph.utility.CmdAssistant;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import static ch.ethz.topobench.graph.utility.CmdAssistant.parseOptions;

public class KShortestPathEvaluatorGenerator implements PathEvaluatorGenerator {

    @Override
    public SelectorResult<PathEvaluator> generate(Graph graph, String[] args) {

        // Parse the options
        Options options = new Options();
        CmdAssistant.addOption(options, "ksp", "k for k-shortest paths (-1 = inf)");
        CmdAssistant.addOption(options, "peprep", "boolean whether it is a preparation run (only 0 or 1 allowed)");
        CommandLine cmd = parseOptions(options, args, true);

        // Read in parameters
        int ksp = ArgumentValidator.retrieveInteger("ksp", cmd.getOptionValue("ksp"));
        boolean peprep = ArgumentValidator.retrieveBoolean("peprep", cmd.getOptionValue("peprep"));

        // Infinity option
        if (ksp == -1) {
            ksp = 1000000;
        }

        // Create path evaluator
        return new SelectorResult<>(new KShortestPathEvaluator(graph, ksp, peprep), cmd.getArgs());

    }

}
