/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.patheval;

import ch.ethz.topobench.Main;
import ch.ethz.topobench.graph.Graph;
import edu.asu.emit.algorithm.graph.Vertex;
import edu.asu.emit.algorithm.graph.algorithms.DijkstraShortestPathAlg;

import java.util.*;

public class ValiantLBPathEvaluator extends PathEvaluator {

    // Mapping of (src, dst) to the set of valid edges
    private final Map<AbstractMap.SimpleEntry<Integer, Integer>, Set<Integer>> validEdges;

    private final int n;

    /**
     * Constructor.
     *
     * @param g     Instance of the graph
     * @param k     Number of valiant nodes that should be used
     */
    public ValiantLBPathEvaluator(Graph g, int k) {
        super(g);

        this.n = g.getNumNodes();

        // Check number of nodes
        if (n > 46339) {
            throw new IllegalArgumentException("n is unfortunately too big for the current implementation.");
        }

        // Check k
        if (k < 1 || k > g.getNumNodes() - 2) {
            throw new RuntimeException("invalid argument for VLB-k; 1 <= k <= n - 2 (given VLB-k is " + k + ").");
        }

        System.out.print(" > KVLB - Calculating shortest path using Dijkstra's algorithm... ");

        // Prepare graph
        GraphCompatibility.printGraphToFile(g, "temp/valiant-lb-converted.txt");
        edu.asu.emit.algorithm.graph.Graph graph = new edu.asu.emit.algorithm.graph.Graph("temp/valiant-lb-converted.txt");
        DijkstraShortestPathAlg dijkstraAlg = new DijkstraShortestPathAlg(graph);

        // Pre-calculate shortest paths
        Map<AbstractMap.SimpleEntry<Integer, Integer>, List<Vertex>> shortestPaths = new HashMap<>();
        for (int src = 0; src < g.getNumNodes(); src++) {
            for (int dst = 0; dst < g.getNumNodes(); dst++) {
                shortestPaths.put(new AbstractMap.SimpleEntry<>(src, dst), dijkstraAlg.getShortestPath(graph.getVertex(src), graph.getVertex(dst)).getVertexList());
            }

            // Print progress percentages
            if (g.getNumNodes() > 10 && (src + 1) % (g.getNumNodes() / 10) == 0) {
                System.out.print(10 * (src + 1) / (g.getNumNodes() / 10) + "%... ");
            }

        }

        System.out.print("part 1 done. ");

        // For every node pair
        this.validEdges = new HashMap<>();
        for (int src = 0; src < g.getNumNodes(); src++) {
            for (int dst = 0; dst < g.getNumNodes(); dst++) {

                // Ignore paths to itself
                if (src == dst) {
                    continue;
                }

                // Calculate n valiant nodes regardless of k
                List<Integer> valiantNodes = new ArrayList<>();
                for (int z = 0; z < g.getNumNodes(); z++) {
                    if (z == src || z == dst) { // Source and destination cannot act as valiant nodes
                        continue;
                    }
                    valiantNodes.add(z);
                }
                Collections.shuffle(valiantNodes, Main.universalRand);

                // Create set in which to save edges (alternative: use boolean[] with edges as indices)
                Set<Integer> localValidEdges = new HashSet<>();

                // Add all the edges on the valiant paths
                for (int z = 0; z < k; z++) {
                    int valiant = valiantNodes.get(z);

                    // Valiant path from src to valiant
                    addAllEdgesOnPath(shortestPaths.get(new AbstractMap.SimpleEntry<>(src, valiant)), localValidEdges);

                    // Valiant path from valiant to dst
                    addAllEdgesOnPath(shortestPaths.get(new AbstractMap.SimpleEntry<>(valiant, dst)), localValidEdges);

                }

                // Update collection of valid edges
                validEdges.put(new AbstractMap.SimpleEntry<>(src, dst), localValidEdges);

            }

            // Print progress percentages
            if (g.getNumNodes() > 10 && (src + 1) % (g.getNumNodes() / 10) == 0) {
                System.out.print(10 * (src + 1) / (g.getNumNodes() / 10) + "%... ");
            }

        }

        System.out.println("part 2 done.");

    }

    /**
     * Add all the edges on the given path to the (src, dst) entry in the global collection
     * of valid edges for (src, dst).
     *
     * @param vertices        Vertex path list
     * @param localValidEdges Local valid edges collection
     */
    private void addAllEdgesOnPath(List<Vertex> vertices, Set<Integer> localValidEdges) {

        // Add all valid edges on the path to the collection
        int from = vertices.get(0).getId();
        for (int z = 1; z < vertices.size(); z++) {
            int to = vertices.get(z).getId();
            localValidEdges.add(from * n + to);
            from = to;
        }

    }

    @Override
    public boolean isFlowZero(int src, int dst, int linkFrom, int linkTo) {
        return !validEdges.get(new AbstractMap.SimpleEntry<>(src, dst)).contains(linkFrom * n + linkTo);
    }

}
