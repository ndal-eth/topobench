/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.traffic;

import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.traffic.generators.*;

import static ch.ethz.topobench.graph.traffic.TrafficSelector.TrafficMode.*;

public class TrafficSelector {

    public enum TrafficMode {
        RAND_PERM_PAIRS,
        ALL_TO_ALL,
        ALL_TO_ALL_FRAC,
        ALL_TO_ALL_FRAC_ORDER,
        ALL_TO_ALL_FRAC_POD_ORDER,
        ALL_TO_ONE,
        STRIDE,
        MIN_WEIGHT_PAIRS,
        MAX_WEIGHT_PAIRS
    }

    /**
     * Map traffic mode to its string representation.
     *
     * @param tm    Traffic mode
     *
     * @return String representation
     */
    private static String getTrafficModeRepresentation(TrafficMode tm) {
        switch(tm) {
            case ALL_TO_ALL:                return "ATA";
            case ALL_TO_ALL_FRAC:           return "ATAF";
            case ALL_TO_ALL_FRAC_ORDER:     return "ATAFO";
            case ALL_TO_ALL_FRAC_POD_ORDER: return "ATAFPO";
            case ALL_TO_ONE:                return "AT1";
            case STRIDE:                    return "STR";
            case RAND_PERM_PAIRS:           return "RPP";
            case MIN_WEIGHT_PAIRS:          return "MIWP";
            case MAX_WEIGHT_PAIRS:          return "MAWP";
            default:                        throw new RuntimeException("TrafficSelector: getTrafficModeRepresentation: cannot select illegal traffic type");
        }
    }

    /**
     * Get the correct traffic mode.
     *
     * @param i     Traffic encoding string
     *
     * @return  Traffic mode
     */
    public static TrafficMode getTrafficMode(String i) {
        switch (i) {
            case "RPP":    return RAND_PERM_PAIRS;
            case "ATA":    return ALL_TO_ALL;
            case "ATAF":   return ALL_TO_ALL_FRAC;
            case "ATAFO":  return ALL_TO_ALL_FRAC_ORDER;
            case "ATAFPO": return ALL_TO_ALL_FRAC_POD_ORDER;
            case "AT1":    return ALL_TO_ONE;
            case "STR":    return STRIDE;
            case "MIWP":   return MIN_WEIGHT_PAIRS;
            case "MAWP":   return MAX_WEIGHT_PAIRS;
            default:       return null;
        }
    }

    /**
     * Generate the traffic for the given traffic mode.
     *
     * @param tm            Traffic mode
     * @param graph         Graph over which to generate traffic
     * @param remainingArgs Remaining arguments (must *all* be applicable, else fails)
     *
     * @return  Resulting traffic
     */
    public static SelectorResult<Traffic> select(TrafficMode tm, Graph graph, String[] remainingArgs) {

        switch(tm) {
            case ALL_TO_ALL:                return new AllToAllTrafficGenerator().generate(graph, remainingArgs);
            case ALL_TO_ALL_FRAC:           return new AllToAllFractionTrafficGenerator().generate(graph, remainingArgs);
            case ALL_TO_ALL_FRAC_ORDER:     return new AllToAllFractionInOrderTrafficGenerator().generate(graph, remainingArgs);
            case ALL_TO_ALL_FRAC_POD_ORDER: return new AllToAllFractionInOrderPodTrafficGenerator().generate(graph, remainingArgs);
            case ALL_TO_ONE:                return new AllToOneTrafficGenerator().generate(graph, remainingArgs);
            case STRIDE:                    return new StrideTrafficGenerator().generate(graph, remainingArgs);
            case RAND_PERM_PAIRS:           return new RandomPermPairsTrafficGenerator().generate(graph, remainingArgs);
            case MIN_WEIGHT_PAIRS:          return new MaxWeightPairsTrafficGenerator(false).generate(graph, remainingArgs);
            case MAX_WEIGHT_PAIRS:          return new MaxWeightPairsTrafficGenerator(true).generate(graph, remainingArgs);
            default:                        throw new IllegalArgumentException("Cannot select illegal traffic type");
        }

    }

    /**
     * Retrieve the list of all traffic modes.
     *
     * @return  String of all traffic modes (e.g. "MAWP, MIWP, RPP")
     */
    public static String getTrafficModes() {
        StringBuilder res = new StringBuilder();
        boolean first = true;
        for (TrafficMode tm : TrafficMode.values()) {
            if (first) {
                first = false;
            } else {
                res.append(", ");
            }
            res.append(getTrafficModeRepresentation(tm));
        }
        return res.toString();
    }

}
