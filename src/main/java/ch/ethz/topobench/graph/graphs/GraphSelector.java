/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.graphs;

import ch.ethz.topobench.graph.Graph;
import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.graphs.generators.*;

import static ch.ethz.topobench.graph.graphs.GraphSelector.Type.*;

public class GraphSelector {

    public enum Type {
        RANDOM_REGULAR_GRAPH,
        FAT_TREE,
        XPANDER,
        TWO_PART_RR_GRAPH,
        FROM_FILE
    }

    /**
     * Map path graph type to its string representation.
     *
     * @param gt    Graph type
     *
     * @return String representation
     */
    private static String getGraphTypeRepresentation(Type gt) {
        switch(gt) {
            case RANDOM_REGULAR_GRAPH:  return "JF";
            case FAT_TREE:              return "FT";
            case XPANDER:               return "XP";
            case TWO_PART_RR_GRAPH:     return "TPRR";
            case FROM_FILE:             return "FILE";
            default:                    throw new RuntimeException("GraphSelector: getGraphTypeRepresentation: cannot select illegal graph type");
        }
    }

    /**
     * Get the correct graph type.
     *
     * @param i     Graph type encoding
     *
     * @return  Graph type
     */
    public static Type getGraphType(String i) {
        switch (i) {
            case "JF":   return RANDOM_REGULAR_GRAPH;
            case "FT":   return FAT_TREE;
            case "XP":   return XPANDER;
            case "TPRR": return TWO_PART_RR_GRAPH;
            case "FILE": return FROM_FILE;
            default: return null;
        }
    }

    /**
     * Generate the graph for the given graph type.
     *
     * @param graphType     Graph type
     * @param remainingArgs Remaining arguments (must *all* be applicable, else fails)
     *
     * @return  Resulting traffic
     */
    public static SelectorResult<Graph> select(Type graphType, String[] remainingArgs) {

        switch(graphType) {
            case RANDOM_REGULAR_GRAPH: return new RandomRegularGraphGenerator().generate(remainingArgs);
            case XPANDER: return new XpanderGraphGenerator().generate(remainingArgs);
            case FAT_TREE: return new FatTreeSigcommGenerator().generate(remainingArgs);
            case TWO_PART_RR_GRAPH: return new TwoPartRRGraphGenerator().generate(remainingArgs);
            case FROM_FILE: return new FromFileGraphGenerator().generate(remainingArgs);
            default: throw new RuntimeException("GraphTypeSelector: select: cannot select illegal graph type");
        }

    }

    /**
     * Retrieve the list of all graph types
     *
     * @return  String of all graph types (e.g. "XP, JF")
     */
    public static String getGraphTypes() {
        StringBuilder res = new StringBuilder();
        boolean first = true;
        for (Type gt : Type.values()) {
            if (first) {
                first = false;
            } else {
                res.append(", ");
            }
            res.append(getGraphTypeRepresentation(gt));
        }
        return res.toString();
    }

}
