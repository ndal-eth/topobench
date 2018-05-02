/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.graphs.generators;

import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.graphs.FatTreeSigcomm;
import ch.ethz.topobench.graph.utility.ArgumentValidator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import static ch.ethz.topobench.graph.utility.CmdAssistant.addOption;
import static ch.ethz.topobench.graph.utility.CmdAssistant.parseOptions;

public class FatTreeSigcommGenerator implements GraphGenerator {

    public SelectorResult<Graph> generate(String[] args) {

        // Parse the options
        Options options = new Options();
        addOption(options, "kft", "k for the k-fat tree");
        CommandLine cmd = parseOptions(options, args, true);

        // Read in parameters
        int k = ArgumentValidator.retrieveInteger("kft", cmd.getOptionValue("kft"));

        // Create graph
        return new SelectorResult<>(new FatTreeSigcomm(k), cmd.getArgs());

    }

}
