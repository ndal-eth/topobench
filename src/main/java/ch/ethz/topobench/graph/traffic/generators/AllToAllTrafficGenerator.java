/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.traffic.generators;

import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.traffic.AllToAllTraffic;
import ch.ethz.topobench.graph.traffic.Traffic;
import ch.ethz.topobench.graph.utility.CmdAssistant;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class AllToAllTrafficGenerator implements TrafficGenerator {

    @Override
    public SelectorResult<Traffic> generate(Graph graph, String[] args) {

        // Parse the options
        CommandLine cmd = CmdAssistant.parseOptions(new Options(), args, false);

        // Return traffic
        return new SelectorResult<>(new AllToAllTraffic(graph), cmd.getArgs());

    }

}
