/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench;

import ch.ethz.topobench.graph.analysis.AnalyzeMCFLP;
import ch.ethz.topobench.graph.analysis.AnalyzeObjective;
import ch.ethz.topobench.graph.analysis.AnalyzeSimpleLPFlow;
import ch.ethz.topobench.graph.analysis.AnalyzeSimpleLPLinkCap;
import ch.ethz.topobench.graph.traffic.TrafficSelector;
import ch.ethz.topobench.graph.utility.CmdAssistant;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AnalyzeSolution {

    public static String ANALYSIS_FOLDER;

    static void main(String args[]) {

        // Read in traffic mode option
        Options options = new Options();
        CmdAssistant.addOption(options, "tm", "trafficmode", true, "traffic mode (" + TrafficSelector.getTrafficModes() + ")");
        CommandLine cmd = CmdAssistant.parseOptions(options, args, false);

        // Now check traffic mode
        String trafficModeEnc = cmd.getOptionValue("trafficmode");
        TrafficSelector.TrafficMode trafficMode = TrafficSelector.getTrafficMode(trafficModeEnc);
        if (trafficMode == null) {
            throw new RuntimeException("FATAL: argument trafficmode does not encode to a valid traffic mode with string " + trafficModeEnc + ".");
        }

        // Create analysis folder
        ANALYSIS_FOLDER = "analysis/" + new SimpleDateFormat("yyyy-MM-dd--HH'h'mm'm'ss's'").format(new Date());
        new File(ANALYSIS_FOLDER).mkdirs();

        // Write analysis
        System.out.print("\nANALYZING SOLUTION\n> Printing analysis into " + ANALYSIS_FOLDER + "...");
        writeAnalysis(trafficMode);
        System.out.println(" done.");

        // Copy files
        System.out.print("> Copying files...");
        Main.runCommand("cp temp/run.info " + ANALYSIS_FOLDER, false);
        Main.runCommand("cp temp/vector.sol " + ANALYSIS_FOLDER, false);
        Main.runCommand("cp temp/topology.txt " + ANALYSIS_FOLDER, false);
        Main.runCommand("cp temp/traffic_pairs.txt " + ANALYSIS_FOLDER, false);
        Main.runCommand("cp temp/node_path_lengths.txt " + ANALYSIS_FOLDER, false);
        if (trafficMode == TrafficSelector.TrafficMode.MAX_WEIGHT_PAIRS || trafficMode == TrafficSelector.TrafficMode.MIN_WEIGHT_PAIRS) {
            Main.runCommand("cp temp/weighed_node_distances.txt " + ANALYSIS_FOLDER, false);
            Main.runCommand("cp temp/weighed_node_distances_mw_fraction.txt " + ANALYSIS_FOLDER, false);
            Main.runCommand("cp temp/max_weight_matching.txt " + ANALYSIS_FOLDER, false);
            Main.runCommand("cp temp/max_weight_matching_sorted.txt " + ANALYSIS_FOLDER, false);
        }
        System.out.println(" done.\n");

    }

    /**
     * Print all the analyses done on the graph and its respective solution.
     *
     * @param tm    Traffic mode
     */
    private static void writeAnalysis(TrafficSelector.TrafficMode tm) {

        // Analyze traffic
        if (Main.USE_SIMPLE_LP) {
            AnalyzeSimpleLPLinkCap.printAnalysis();
            AnalyzeSimpleLPFlow.printAnalysis();
        } else {
            AnalyzeMCFLP.printAnalysis();
        }

        // Print objective
        AnalyzeObjective.printAnalysis("temp/objective.txt");
        AnalyzeObjective.printAnalysis(ANALYSIS_FOLDER + "/objective.txt");

    }

}
