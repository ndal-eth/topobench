package ch.ethz.topobench;

import ch.ethz.topobench.graph.utility.ArgumentValidator;
import ch.ethz.topobench.graph.utility.Configuration;
import ch.ethz.topobench.graph.utility.CmdAssistant;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

public class Main {

    // Make it so that there's only one source of randomness in all, so I can control it if need be.
    public static Random universalRand;

    // These are variables that are set in the run.config file
    public static int K_UPPER_BOUND;
    private static boolean SILENT_COMMAND;
    public static int PYTHON_VERSION;
    public static String SORT_COMMAND;
    public static final int MAX_SHORTEST_PATH_LENGTH_POSSIBLE = 10000;
    private static long seed;
    private static boolean seedIsSet = false;
    public static boolean USE_SIMPLE_LP = true;

    /**
     * Main method called with required arguments.
     *
     * @param args  Un-parsed array of arguments
     */
    public static void main(String args[]) {

        // Initialize from run.config
        loadRunEnvironmentConfiguration();

        // Parse arguments
        Options options = new Options();
        CmdAssistant.addOption(options, "m", "mode", true, "mode (PRODUCE or ANALYZE)");
        CmdAssistant.addOption(options, "seed", "seed", true, "random seed (0 for random)");
        CmdAssistant.addOption(options, "lpt", "linearprogramtype", true, "linear program type (SIMPLE, MCFFC)");
        CommandLine cmd = CmdAssistant.parseOptions(options, args, true);

        // Retrieve arguments
        long randSeed = ArgumentValidator.retrieveLong("seed", cmd.getOptionValue("seed"));
        String mode = cmd.getOptionValue("mode");

        // Determine linear program type
        switch (cmd.getOptionValue("linearprogramtype")) {
            case "SIMPLE": {
                USE_SIMPLE_LP = true;
                break;
            }
            case "MCFFC": {
                USE_SIMPLE_LP = false;
                break;
            }
            default: {
                throw new RuntimeException("Main: main: invalid linear program type (allowed values: SIMPLE, MCFFC)");
            }
        }

        // Log start of mode
        System.out.println(
                "\n" +
                "------------------------------------\n" +
                "----- TOPOBENCH MODE: " + mode + "\n" +
                "------------------------------------\n"
        );

        // Set random generator
        initGlobalRandomness(randSeed);

        // Call appropriate mode
        switch (mode) {
            case "PRODUCE": {
                ProduceLP.main(cmd.getArgs());
                break;
            }
            case "ANALYZE": {
                AnalyzeSolution.main(cmd.getArgs());
                break;
            }
            default: {
                System.out.println("Invalid mode: " + mode + " (must be either PRODUCE or ANALYZE).");
                break;
            }
        }

        // Log end of mode
        System.out.println(
                "------------------------------------\n" +
                "----- END OF MODE\n" +
                "------------------------------------\n"
        );

    }

    /**
     * Initialize all global configuration (e.g. python version, sort command)
     * from the run.config file.
     */
    public static void loadRunEnvironmentConfiguration() {
        Configuration configuration = new Configuration("run.config");
        SILENT_COMMAND = Integer.valueOf(configuration.get("silent_command")) == 1;
        K_UPPER_BOUND = Integer.valueOf(configuration.get("k_upper_bound"));
        PYTHON_VERSION = Integer.valueOf(configuration.get("python_version"));
        SORT_COMMAND = configuration.get("sort_command");
    }

    /**
     * Set the random seed used for all randomness in the program.
     *
     * @param seed  Random seed long integer
     */
    public static void initGlobalRandomness(long seed) {

        // Set the seed
        Main.seed = seed;
        boolean isRandomSeed = false;
        if (Main.seed == 0) {
            Main.universalRand = new Random();
            Main.seed = Math.abs(universalRand.nextInt()); // Next integer, because Xpander random seed must be positive integer
            isRandomSeed = true;
        }

        // Create random number generator
        Main.universalRand = new Random(Main.seed);

        // Seed has been set
        seedIsSet = true;

        // Log random initialization
        System.out.println("RNG: Random seed has been set " + (isRandomSeed ? "randomly" : "deterministically") + " to " + Main.seed + ".");

    }

    /**
     * Run a command in the prompt (e.g. to call a python script).
     * Error write output is always shown.
     * Normal write output is not shown.
     *
     * @param cmd           Command
     */
    public static void runCommand(String cmd) {
        runCommand(cmd, false);
    }

    /**
     * Run a command in the prompt (e.g. to call a python script).
     * Error write output is always shown.
     *
     * @param cmd           Command
     * @param showOutput    Whether to show the normal write output from the command in the console
     */
    public static void runCommand(String cmd, boolean showOutput) {

        Process p;
        try {

            if (!SILENT_COMMAND) {
                System.out.println("Running command \"" + cmd + "\"...");
            }

            // Start process
            p = Runtime.getRuntime().exec(cmd);

            // Fetch input streams
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // Read the output from the command
            String s;
            while ((s = stdInput.readLine()) != null && !showOutput) {
                System.out.println(s);
            }

            // Read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            // Wait for the command thread to be ended
            p.waitFor();
            p.destroy();

            if (!SILENT_COMMAND) {
                System.out.println("... command has been executed (any output is shown above).");
            }

        } catch (Exception e) {
            throw new RuntimeException("Command failed: " + cmd);
        }

    }

    /**
     * Retrieve the set random seed.
     *
     * @return  Random seed set
     */
    public static long getRandomSeed() {
        if (!seedIsSet) {
            throw new RuntimeException("Main: getRandomSeed: seed has not yet been set.");
        }
        return seed;
    }

}
