/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.patheval;

import ch.ethz.topobench.graph.Graph;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.Vertex;
import edu.asu.emit.algorithm.graph.algorithms.YenTopKShortestPathsAlg;

import java.io.*;
import java.util.*;

/**
 * K-shortest path evaluator using Yen's algorithm.
 *
 * Gracefully uses the Java library made available by Yan Qi of Arizona State University.
 * GitHub: https://github.com/yan-qi/k-shortest-paths-java-version
 */
public class KShortestPathEvaluator extends PathEvaluator {

    // Mapping of (src, dst) to the set of valid edges
    private final Map<AbstractMap.SimpleEntry<Integer, Integer>, Set<AbstractMap.SimpleEntry<Integer, Integer>>> validEdges;

    /**
     * Constructor for the K-shortest path evaluator.
     * Calls upon the Yen's algorithm library to pre-compute the paths.
     *
     * @param g     Graph
     * @param k     K in the K-shortest path algorithm (which paths to consider)
     * @param prep  Whether it is the preparation run to first calculate a high k
     */
    public KShortestPathEvaluator(Graph g, int k, boolean prep) {
        super(g);

        if (k <= 0) {
            throw new RuntimeException("KShortestPathEvaluator: constructor: invalid argument; k must be >= 1 (k=" + k + ")");
        }

        // Convert internal graph version to that of library
        GraphCompatibility.printGraphToFile(g, "temp/k-shortest-path-graph-converted.txt");
        edu.asu.emit.algorithm.graph.Graph graph = new edu.asu.emit.algorithm.graph.Graph("temp/k-shortest-path-graph-converted.txt");

        // Batch process initialize yen's algorithm
        YenTopKShortestPathsAlg yenAlg = new YenTopKShortestPathsAlg(graph);

        System.out.print(" > " + (prep ? "Pre-c" : "C") + "alculating K shortest paths using Yen's algorithm... ");

        // Initialize valid links mapping
        this.validEdges = new HashMap<>();
        for (int i = 0; i < g.getNumNodes(); i++) {
            for (int j = 0; j < g.getNumNodes(); j++) {
                validEdges.put(new AbstractMap.SimpleEntry<>(i, j), new HashSet<>());
            }
        }

        // If it is the preparation run, for a large k determine the paths,
        // such that they can be used for lower k
        if (prep) {
            printKShortestPathEdges("temp/k-shortest-valid-edges-cache.txt", g, yenAlg, graph, k);
        }

        // Load from cache using k as threshold
        loadKShortestPathEdges("temp/k-shortest-valid-edges-cache.txt", k);

        System.out.println(" done.");

    }


    /**
     * Load from the given file all the valid edges into buffer.
     *
     * The input file has the following structure:
     * [source id] [destination id] [edge-from id] [edge-to id] [required-k]
     *
     * Only the valid edges which have the required-k <= k are logically added.
     *
     * @param fileName  File name
     * @param k         How many shortest acyclic paths are taken into account (K shortest path)
     */
    private void loadKShortestPathEdges(String fileName, int k) {

        try {

            // Open input stream
            FileInputStream fileStream = new FileInputStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));

            // Simply read in the server pairs
            String strLine;
            while ((strLine = br.readLine()) != null) {

                // Split up sentences
                String[] match = strLine.split(" ");
                int src = Integer.valueOf(match[0]);
                int dst = Integer.valueOf(match[1]);
                int from = Integer.valueOf(match[2]);
                int to = Integer.valueOf(match[3]);
                int pn = Integer.valueOf(match[4]);

                if (pn <= k) {
                    validEdges.get(new AbstractMap.SimpleEntry<>(src, dst)).add(new AbstractMap.SimpleEntry<>(from, to));
                }
            }

            // Close input stream
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Print for a large K all valid edges for any lower than or equal k.
     *
     * The output file has the following structure:
     * [source id] [destination id] [edge-from id] [edge-to id] [required-k]
     *
     * @param fileName      File name
     * @param g             Original graph
     * @param yenAlg        Instance of Yen's Algorithm solver
     * @param graph         Compatible graph representation for Yen's algorithm
     * @param k             Large K
     */
    private void printKShortestPathEdges(String fileName, Graph g, YenTopKShortestPathsAlg yenAlg, edu.asu.emit.algorithm.graph.Graph graph, int k) {

        try {

            // Open output stream
            FileWriter fileStream = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(fileStream);

            // Go over every (src, dst) pair
            for (int i = 0; i < g.getNumNodes(); i++) {
                for (int j = 0; j < g.getNumNodes(); j++) {

                    // Can't go to itself
                    if (i == j) {
                        continue;
                    }

                    // Perform Yen's algorithm with the source and destination
                    List<Path> pathList = yenAlg.getShortestPaths(graph.getVertex(i), graph.getVertex(j), k);

                    // Initialize map
                    Set<AbstractMap.SimpleEntry<Integer, Integer>> localValidEdges = new HashSet<>();

                    // Add all the edges to the mapping
                    int pn = 1;
                    for (Path p : pathList) {
                        List<Vertex> vertices = p.getVertexList();
                        int from = vertices.get(0).getId();
                        for (int z = 1; z < vertices.size(); z++) {
                            int to = vertices.get(z).getId();

                            if (!localValidEdges.contains(new AbstractMap.SimpleEntry<>(from, to))) {
                                out.write(i + " " + j + " " + from + " " + to + " " + pn + "\n");
                            }

                            localValidEdges.add(new AbstractMap.SimpleEntry<>(from, to));
                            from = to;
                        }
                        pn++;
                    }

                }

                // Print progress percentages
                if (g.getNumNodes() > 10 && (i + 1) % (g.getNumNodes() / 10) == 0) {
                    System.out.print(10 * (i + 1) / (g.getNumNodes() / 10) + "%... ");
                }

            }

            // Close output stream
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Only allow links on the K-shortest paths from the source to destination.
     *
     * @param src           Source of flow
     * @param dst           Destination of flow
     * @param linkFrom      i-component of link
     * @param linkTo        j-component of link
     *
     * @return  True iff it is not on a K-shortest path
     */
    @Override
    public boolean isFlowZero(int src, int dst, int linkFrom, int linkTo) {
        return !validEdges.get(new AbstractMap.SimpleEntry<>(src, dst)).contains(new AbstractMap.SimpleEntry<>(linkFrom, linkTo));
    }

}
