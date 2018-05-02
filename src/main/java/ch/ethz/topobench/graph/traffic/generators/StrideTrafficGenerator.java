/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.traffic.generators;

import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.traffic.StrideTraffic;
import ch.ethz.topobench.graph.traffic.Traffic;
import ch.ethz.topobench.graph.utility.ArgumentValidator;
import ch.ethz.topobench.graph.utility.CmdAssistant;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class StrideTrafficGenerator implements TrafficGenerator {

    @Override
    public SelectorResult<Traffic> generate(Graph graph, String[] args) {

        // Parse the options
        Options options = new Options();
        CmdAssistant.addOption(options, "str", "stride", true, "server stride (must not be divisible by number of servers)");
        CommandLine cmd = CmdAssistant.parseOptions(options, args, false);

        // Retrieve parameters
        int stride = ArgumentValidator.retrieveInteger("stride", cmd.getOptionValue("stride"));

        // Return traffic
        return new SelectorResult<>(new StrideTraffic(graph, stride), cmd.getArgs());

    }

}
