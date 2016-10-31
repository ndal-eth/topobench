/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.graphs.generators;

import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.graphs.TwoPartRRGraph;
import ch.ethz.topobench.graph.utility.ArgumentValidator;
import ch.ethz.topobench.graph.Graph;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import static ch.ethz.topobench.graph.utility.CmdAssistant.addOption;
import static ch.ethz.topobench.graph.utility.CmdAssistant.parseOptions;

public class TwoPartRRGraphGenerator implements GraphGenerator {

    public SelectorResult<Graph> generate(String[] args) {

        Options options = new Options();
        addOption(options, "switches", "number of switches");
        addOption(options, "switchports", "number of ports per switch");
        addOption(options, "netports", "number of ports per switch for networking");
        addOption(options, "partfrac", "fraction of active nodes in the traffic matrix");
        addOption(options, "eA2A", "number of external network port switches in the A2A part");
        addOption(options, "eSUPP", "number of external network port switches in the SUPP part");
        CommandLine cmd = parseOptions(options, args, true);

        // Read in parameters
        int switches = Integer.parseInt(cmd.getOptionValue("switches"));
        int switchports = Integer.parseInt(cmd.getOptionValue("switchports"));
        int netports = Integer.parseInt(cmd.getOptionValue("netports"));
        int serverports = switchports - netports;
        double fraction = ArgumentValidator.retrieveDouble("partfrac", cmd.getOptionValue("partfrac"));
        int eA2A = ArgumentValidator.retrieveInteger("eA2A", cmd.getOptionValue("eA2A"));
        int eSUPP = ArgumentValidator.retrieveInteger("eSUPP", cmd.getOptionValue("eSUPP"));

        // Create graph
        return new SelectorResult<>(new TwoPartRRGraph(netports, serverports, switches, fraction, eA2A, eSUPP), cmd.getArgs());

    }

}
