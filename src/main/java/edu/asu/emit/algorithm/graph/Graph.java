/*
 *
 * Copyright (c) 2004-2008 Arizona State University.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ARIZONA STATE UNIVERSITY ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL ARIZONA STATE UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package edu.asu.emit.algorithm.graph;

import edu.asu.emit.algorithm.utils.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * The class defines a directed graph.
 * 
 * @author yqi
 * @author snkas
 */
public class Graph implements BaseGraph {

    public static final long DISCONNECTED = 1000000000;

    // Maps a vertex identifier to all vertices a directed edges exists from itself to them
    private Map<Integer, List<Vertex>> outEdges = new HashMap<>();

    // Maps a vertex identifier to all vertices a directed edges exists to itself from them
    private Map<Integer, List<Vertex>> inEdges = new HashMap<>();

    // Maps a pair of vertex identifiers (an edge) to its weight
    private Map<Pair<Integer, Integer>, Long> edgeWeights = new HashMap<>();

    // Maps a vertex identifier to its vertex object
    private Map<Integer, Vertex> idVertexIndex = new HashMap<>();

    // List of all vertices in the graph
    private List<Vertex> vertexList = new Vector<>();

    // The number of vertices in the graph
    private int numVertices = 0;

    // The number of edges in the graph
    private int numEdges = 0;

    /**
     * Constructor to create the graph from a file.
     *
     * Required file structure:
     * <pre>
     * [number of nodes]
     *
     * [nid1] [nid2] [weight]
     * [nid1] [nid2] [weight]
     * [nid1] [nid2] [weight]
     * ...
     * </pre>
     *
     * @param fileName File name
     */
    public Graph(final String fileName) {
        importFromFile(fileName);
    }

    /**
     * Constructor to create a shallow copy of another graph.
     *
     * @param graph Graph instance
     */
	public Graph(Graph graph) {
		numVertices = graph.numVertices;
		numEdges = graph.numEdges;
		vertexList.addAll(graph.vertexList);
		idVertexIndex.putAll(graph.idVertexIndex);
		inEdges.putAll(graph.inEdges);
		outEdges.putAll(graph.outEdges);
		edgeWeights.putAll(graph.edgeWeights);
	}

    /**
     * Read in graph from file.
     *
     * @param fileName File name
     */
    private void importFromFile(String fileName) {

        try {

            // Open file stream
            FileReader input = new FileReader(fileName);
            BufferedReader br = new BufferedReader(input);

            // Go over all lines
            boolean isFirstLine = true;
            String line;
            while ((line = br.readLine()) != null) {

                // Skip empty lines
                if (line.trim().equals("")) {
                    continue;
                }

                if (isFirstLine) { // Number of vertices

                    isFirstLine = false;
                    numVertices = Integer.parseInt(line.trim());
                    for (int i = 0; i < numVertices; i++) {
                        Vertex vertex = new Vertex(i);
                        vertexList.add(vertex);
                        idVertexIndex.put(i, vertex);
                        inEdges.put(i, new ArrayList<>());
                        outEdges.put(i, new ArrayList<>());
                    }

                } else { // Edge

                    // Retrieve edge components
                    String[] strList = line.trim().split("\\s");
                    int startVertexId = Integer.parseInt(strList[0]);
                    int endVertexId = Integer.parseInt(strList[1]);
                    long weight = Long.parseLong(strList[2]);

                    // Add edge
                    addEdge(startVertexId, endVertexId, weight);

                }
            }

            // Close file stream
            br.close();

        } catch (IOException e) {
            throw new RuntimeException("Graph: importFromFile: failed to read file: " + e.getMessage());
        }

    }

	/**
	 * Add edge (start, end): weight to the graph.
	 *
	 * @param startVertexId     Start vertex identifier of the edge
	 * @param endVertexId       End vertex identifier of the edge
	 * @param weight            Edge weight
	 */
	private void addEdge(int startVertexId, int endVertexId, long weight) {

        // Check that the vertex identifiers exist
        if (!idVertexIndex.containsKey(startVertexId) || !idVertexIndex.containsKey(endVertexId) || startVertexId == endVertexId) {
            throw new IllegalArgumentException("Graph: addEdge: the edge (" + startVertexId + ", " + endVertexId + ") does not exist in the graph.");
        }

        // Check that the edge does not already exist
        if (edgeWeights.containsKey(new Pair<>(startVertexId, endVertexId))) {
            throw new IllegalArgumentException("Graph: addEdge: the edge (" + startVertexId + ", " + endVertexId + ") already exists.");
        }

        // Add to inward and outward edge list
        outEdges.get(startVertexId).add(idVertexIndex.get(endVertexId));
        inEdges.get(endVertexId).add(idVertexIndex.get(startVertexId));

        // Store into edge weight map
        edgeWeights.put(new Pair<>(startVertexId, endVertexId), weight);

        // Increase counter for number of edges
        numEdges++;

    }

    /**
     * Retrieve all vertices that have an edge incoming from the given vertex.
     *
     * @param vertex    Vertex instance
     *
     * @return  List of vertices that have edge incoming from the vertex
     */
    @Override
    public List<Vertex> getAdjacentVertices(Vertex vertex) {
        return outEdges.containsKey(vertex.getId()) ? outEdges.get(vertex.getId()) : new ArrayList<>();
    }

    /**
     * Retrieve all vertices that have an edge toward the given vertex.
     *
     * @param vertex    Vertex instance
     *
     * @return  List of vertices that have edge towards it
     */
    @Override
    public List<Vertex> getPrecedentVertices(Vertex vertex) {
        return inEdges.containsKey(vertex.getId()) ? inEdges.get(vertex.getId()) : new ArrayList<>();
    }

    /**
     * Retrieve the weight of directed edge (src, dst).
     *
     * @param source    Source vertex
     * @param target    Destination vertex
     *
     * @return  Edge weight
     */
    @Override
    public long getEdgeWeight(Vertex source, Vertex target) {
        if (edgeWeights.containsKey(new Pair<>(source.getId(), target.getId()))) {
            return edgeWeights.get(new Pair<>(source.getId(), target.getId()));
        } else {
            throw new RuntimeException("Graph: getEdgeWeight: cannot retrieve edge weight of non-existing edge.");
        }
    }

    /**
     * Return the vertex list in the graph.
     *
     * @return  List of all vertices in the graph
     */
    @Override
    public List<Vertex> getVertexList() {
        return vertexList;
    }

    /**
     * Get the vertex with the corresponding identifier.
     *
     * @param id    Vertex identifier
     *
     * @return  Vertex instance (if not found, throws RuntimeException)
     */
    public Vertex getVertex(int id) {
        if (idVertexIndex.containsKey(id)) {
            return idVertexIndex.get(id);
        } else {
            throw new RuntimeException("Graph: getVertex: cannot retrieve vertex for invalid identifier.");
        }
    }
    
}
