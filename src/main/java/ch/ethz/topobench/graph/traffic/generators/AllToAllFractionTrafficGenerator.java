/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.traffic.generators;

import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.traffic.AllToAllFractionTraffic;
import ch.ethz.topobench.graph.traffic.Traffic;
import ch.ethz.topobench.graph.utility.ArgumentValidator;
import ch.ethz.topobench.graph.utility.CmdAssistant;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class AllToAllFractionTrafficGenerator implements TrafficGenerator {

    @Override
    public SelectorResult<Traffic> generate(Graph graph, String[] args) {

        // Parse the options
        Options options = new Options();
        CmdAssistant.addOption(options, "tfr", "trafficfraction", true, "traffic fraction of participating servers; range: [0.0, 1,0]");
        CommandLine cmd = CmdAssistant.parseOptions(options, args, false);

        // Retrieve parameters
        double fraction = ArgumentValidator.retrieveDouble("trafficfraction", cmd.getOptionValue("tfr"));

        // Return traffic
        return new SelectorResult<>(new AllToAllFractionTraffic(graph, fraction), cmd.getArgs());

    }

}
