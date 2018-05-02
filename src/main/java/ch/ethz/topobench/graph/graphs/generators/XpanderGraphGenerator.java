/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.graphs.generators;

import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.graphs.XpanderGraph;
import ch.ethz.topobench.graph.utility.CmdAssistant;
import ch.ethz.topobench.graph.Graph;
import org.apache.commons.cli.*;

import static ch.ethz.topobench.graph.utility.CmdAssistant.addOption;

public class XpanderGraphGenerator implements GraphGenerator {

    public SelectorResult<Graph> generate(String[] args) {

        Options options = new Options();
        CmdAssistant.addOption(options, "switches", "number of switches");
        CmdAssistant.addOption(options, "switchports", "number of ports per switch");
        CmdAssistant.addOption(options, "netports", "number of ports per switch for networking");
        CommandLine cmd = CmdAssistant.parseOptions(options, args, true);

        // Read in parameters
        int switches = Integer.parseInt(cmd.getOptionValue("switches"));
        int switchports = Integer.parseInt(cmd.getOptionValue("switchports"));
        int netports = Integer.parseInt(cmd.getOptionValue("netports"));
        int serverports = switchports - netports;

        // Create graph
        return new SelectorResult<>(new XpanderGraph(netports, serverports, switches), cmd.getArgs());

    }

}
