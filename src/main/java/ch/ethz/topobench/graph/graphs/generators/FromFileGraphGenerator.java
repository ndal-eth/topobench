/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.graphs.generators;

import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.graphs.FileBidirGraph;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import static ch.ethz.topobench.graph.utility.CmdAssistant.addOption;
import static ch.ethz.topobench.graph.utility.CmdAssistant.parseOptions;

public class FromFileGraphGenerator implements GraphGenerator {

    public SelectorResult<Graph> generate(String[] args) {

        // Parse the options
        Options options = new Options();
        addOption(options, "switches", "number of switches");
        addOption(options, "partswitches", "number of participating switches (implies interval of identifiers [0, numb))");
        addOption(options, "filename", "file name");
        CommandLine cmd = parseOptions(options, args, true);

        // Read in parameters
        int switches = Integer.parseInt(cmd.getOptionValue("switches"));
        int partSwitches = Integer.parseInt(cmd.getOptionValue("partswitches"));
        String file = cmd.getOptionValue("filename");

        // Print what we are doing
        System.out.println("Producing Linear Program for Graph from File, parameters: ");
        System.out.println("\t> Switches: " + switches);
        System.out.println("\t> Participating switch identifiers: [0, " + partSwitches + ")");
        System.out.println("\t> Source file: " + file);
        System.out.println("\t> Producing graph...");

        // Create graph
        return new SelectorResult<>(new FileBidirGraph(switches, partSwitches, file), cmd.getArgs());

    }

}
