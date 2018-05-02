/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.graphs.generators;

import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.graphs.RandomRegularGraph;
import ch.ethz.topobench.graph.Graph;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import static ch.ethz.topobench.graph.utility.CmdAssistant.addOption;
import static ch.ethz.topobench.graph.utility.CmdAssistant.parseOptions;

public class RandomRegularGraphGenerator implements GraphGenerator {

    public SelectorResult<Graph> generate(String[] args) {

        // Parse the options
        Options options = new Options();
        addOption(options, "switches", "number of switches");
        addOption(options, "switchports", "number of ports per switch");
        addOption(options, "netports", "number of ports per switch for networking");
        CommandLine cmd = parseOptions(options, args, true);

        // Read in parameters
        int switches = Integer.parseInt(cmd.getOptionValue("switches"));
        int switchports = Integer.parseInt(cmd.getOptionValue("switchports"));
        int netports = Integer.parseInt(cmd.getOptionValue("netports"));
        int serverports = switchports - netports;

        // Print what we are doing
        System.out.println("Producing Linear Program for Random Regular Graph (a.k.a. JellyFish); parameters: ");
        System.out.println("\t> Switches: " + switches);
        System.out.println("\t> Total ports per switch: " + switchports);
        System.out.println("\t> Server ports per switch: " + serverports);
        System.out.println("\t> Networking ports per switch (graph degree): " + netports);
        System.out.println("\t> Producing graph...");

        // Create graph
        return new SelectorResult<>(new RandomRegularGraph(switches, switchports, netports), cmd.getArgs());

    }

}
