/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.utility;

import org.apache.commons.cli.*;

public class CmdAssistant {

    /**
     * Parse the options. Fails if it does not adhere to requirements.
     *
     * @param options   Options set for the command line
     * @param args      Input from command line
     * @param allowUnknownOptsAtEnd     Whether to allow unknown commands at end
     *
     * @return  Parsed command line arguments
     */
    public static CommandLine parseOptions(Options options, String[] args, boolean allowUnknownOptsAtEnd) {

        // Parse the arguments
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args, allowUnknownOptsAtEnd);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("ProduceLP", options);
            throw new RuntimeException("Failed to generated graph due to missing options.");
        }

        return cmd;

    }

    /**
     * Add a required option to the options with the given name and description.
     *
     * @param options   Options set for the command line
     * @param arg       Argument name (- variant)
     * @param desc      Description
     */
    public static void addOption(Options options, String arg, String desc) {
        Option opt = new Option(arg, true, desc);
        opt.setRequired(true);
        options.addOption(opt);
    }

    /**
     * Add an option to the options.
     *
     * @param options   Options set for the command line
     * @param argShort  Short argument (-variant)
     * @param argLong   Long argument (-- variant)
     * @param required  Whether it is required
     * @param desc      Description
     */
    public static void addOption(Options options, String argShort, String argLong, boolean required, String desc) {
        Option opt = new Option(argShort, argLong, true, desc);
        opt.setRequired(required);
        options.addOption(opt);
    }

}
