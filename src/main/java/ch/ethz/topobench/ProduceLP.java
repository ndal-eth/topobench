/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench;

import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.graphs.GraphSelector;
import ch.ethz.topobench.graph.patheval.PathEvaluator;
import ch.ethz.topobench.graph.patheval.PathEvaluatorSelector;
import ch.ethz.topobench.graph.print.*;
import ch.ethz.topobench.graph.traffic.Traffic;
import ch.ethz.topobench.graph.traffic.TrafficPair;
import ch.ethz.topobench.graph.traffic.TrafficSelector;
import ch.ethz.topobench.graph.utility.CmdAssistant;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.io.File;
import java.util.List;

import static ch.ethz.topobench.Main.loadRunEnvironmentConfiguration;

class ProduceLP {

    /**
     * Main method called with required arguments.
     *
     * @param args  Un-parsed array of arguments
     */
    static void main(String args[]) {

        // Initialize
        loadRunEnvironmentConfiguration();

        Options options = new Options();

        // Required options
        CmdAssistant.addOption(options, "gt", "graphtype", true, "graph type (" + GraphSelector.getGraphTypes() + ")");
        CmdAssistant.addOption(options, "tm", "trafficmode", true, "traffic mode (" + TrafficSelector.getTrafficModes() + ")");
        CmdAssistant.addOption(options, "pe", "pathevaluator", true, "path evaluator (" + PathEvaluatorSelector.getPathEvaluators() + ")");

        // Parse all options
        CommandLine cmd = CmdAssistant.parseOptions(options, args, true);

        // Now check graph type
        String graphTypeEnc = cmd.getOptionValue("graphtype", "INVALID");
        GraphSelector.Type graphType = GraphSelector.getGraphType(graphTypeEnc);
        if (graphType == null) {
            throw new RuntimeException("FATAL: argument graphType does not encode to a valid graph type with string " + graphTypeEnc + ".");
        }

        // Now check traffic mode
        String trafficModeEnc = cmd.getOptionValue("trafficmode", "INVALID");
        TrafficSelector.TrafficMode trafficMode = TrafficSelector.getTrafficMode(trafficModeEnc);
        if (trafficMode == null) {
            throw new RuntimeException("FATAL: argument trafficMode does not encode to a valid traffic mode with string " + trafficModeEnc + ".");
        }

        // Now check path evaluator
        String pathEvaluatorEnc = cmd.getOptionValue("pathevaluator", "INVALID");
        PathEvaluatorSelector.Type pathEvaluator = PathEvaluatorSelector.getPathEvaluator(pathEvaluatorEnc);
        if (pathEvaluator == null) {
            throw new RuntimeException("FATAL: argument pathEvaluator does not encode to a valid path evaluator with string " + pathEvaluatorEnc + ".");
        }

        // Finally, run the show
        produce(graphType, trafficMode, pathEvaluator, cmd.getArgs(), args);

    }

    /**
     * Produce the linear program and all that comes with it.
     *
     * @param graphType         Graph type
     * @param trafficMode       Traffic mode
     * @param pathEvaluatorType Path evaluator type
     * @param remainingArgs     All remaining arguments
     * @param allArgs           All arguments given
     */
    private static void produce(
            GraphSelector.Type graphType,
            TrafficSelector.TrafficMode trafficMode,
            PathEvaluatorSelector.Type pathEvaluatorType,
            String[] remainingArgs,
            String[] allArgs
    ) {

        // Print what we are doing
        System.out.println(
                "\nGraphType:         " + graphType + "\n" +
                "Traffic Mode:      " + trafficMode + "\n" +
                "Path Evaluator:    " + pathEvaluatorType + "\n"
        );

        System.out.println("\nCLEANING TEMPORARY FOLDERS");

        // Performing complete clean-up
        new File("temp/final").mkdirs();
        new File("temp/graph").mkdirs();
        new File("temp/lp").mkdirs();
        new File("temp/traffic").mkdirs();
        new File("temp/cache").mkdirs();
        removeFilesIn("temp/final", "temp/graph", "temp/lp", "temp/traffic");
        System.out.println(" > Temporary folders have been cleaned.");

        // Generate graph
        System.out.println("\nGRAPH GENERATION");
        System.out.println(" > Generating graph...");
        SelectorResult<Graph> graphSelectorResult = GraphSelector.select(graphType, remainingArgs);
        Graph graph = graphSelectorResult.getResult();

        // Generate path evaluator
        System.out.println("\nPATH EVALUATOR GENERATION");
        System.out.println(" > Loading path evaluator...");
        SelectorResult<PathEvaluator> pathEvaluatorSelectorResult = PathEvaluatorSelector.select(pathEvaluatorType, graph, graphSelectorResult.getRemainingArgs());
        PathEvaluator pathEvaluator = pathEvaluatorSelectorResult.getResult();

        // Generate appropriate traffic
        System.out.println("\nTRAFFIC GENERATION");
        System.out.println(" > Generating traffic...");
        SelectorResult<Traffic> trafficSelectorResult = TrafficSelector.select(trafficMode, graph, pathEvaluatorSelectorResult.getRemainingArgs());
        List<TrafficPair> traffic = trafficSelectorResult.getResult().generate();

        // Print topology information
        System.out.println("\nPRINTING");
        System.out.print(" > Printing graph information...");
        PrinterTrafficPairs.print("temp/final/traffic_pairs.txt", traffic);
        new PrinterGraph(graph).print("temp/final/topology.txt");
        new PrinterPathLengths(graph).print("temp/final/node_path_lengths.txt");
        PrinterRun.print("temp/final/run.info", allArgs);
        System.out.println(" done.");

        // Print the linear program
        writeLP(graph, trafficMode, traffic, pathEvaluator);

    }

    /**
     * Deletion of a folder and all its sub-folders
     *
     * Adapted from:
     * http://stackoverflow.com/questions/7768071/how-to-delete-directory-content-in-java
     *
     * @param folders    Folder to delete
     */
    private static void removeFilesIn(String... folders) {
        for (String fol : folders) {
            File folder = new File(fol);
            File[] files = folder.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        f.delete();
                    }
                }
            }
        }
    }

    /**
     * Write the linear program.
     *
     * @param g         Graph
     * @param tm        Traffic mode
     * @param traffic   Traffic matrix
     * @param pathEvaluator     Path evaluator
     */
    private static void writeLP(Graph g, TrafficSelector.TrafficMode tm, List<TrafficPair> traffic, PathEvaluator pathEvaluator) {

        if (Main.USE_SIMPLE_LP) {
            System.out.print(" > Printing Simple linear program...");
            new PrinterSimple(g).print(traffic);
        } else {
            System.out.print(" > Printing MCF Fair Condensed linear program...");
            new PrinterMCFFairCondensed(g, pathEvaluator).print(traffic);
        }
        System.out.println(" done.\n");
    }

}
