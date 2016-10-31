/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.graphs;

import ch.ethz.topobench.graph.Graph;

import java.util.Vector;

public class RandomRegularGraph extends Graph {

    private static final int MAX_ITERATION_LINK_SEARCH = 1000;

    /**
     * Construct a random regular graph.
     *
     * @param n                 Number of nodes
     * @param switchDegree      Out-degree of switch (total number of ports)
     * @param networkDegree     Network degree of switch (total number of ports used for networking)
     */
    public RandomRegularGraph(int n, int switchDegree, int networkDegree) {
        super("Random Regular Graph", n, switchDegree - networkDegree);
        populateAdjacencyList(switchDegree, networkDegree);
    }

    /**
     * Populate the adjacency list of the RRG.
     *
     * @param switchDegree      Total amount of ports on the switch
     * @param networkDegree     Amount of ports on the switch destined for networking (other are server ports)
     */
    private void populateAdjacencyList(int switchDegree, int networkDegree) {

        // Print graph creation information
        System.out.println(
                "Creating RRG (n=" + numNodes + ") with network degree " + networkDegree +
                " and server degree " + (switchDegree - networkDegree) +
                " supporting " + getTotalWeight() + " servers."
        );

        // Initialize keeping track of nodes to link
        Vector<Integer> stillToLink = new Vector<>(numNodes);
        Vector<Integer> degreeUsedUp = new Vector<>(numNodes);
        for (int i = 0; i < numNodes; i++) {
            stillToLink.add(i);   // In the beginning, all are available to link
            degreeUsedUp.add(0);  // In the beginning, no edges exist, so no degree is used up
        }

        /*
         * In each loop, create a new link;
         * It stops when one of the following conditions is true:
         *
         * (a) It was impossible to find a link (due to a clique forming)
         * (b) Only a single vertex remains with some degree left over
         * (c) All vertices have all their degrees satisfied
        */
        boolean stopSign = false;
        while (!stillToLink.isEmpty() && !stopSign) {

            // Link pair variables
            int p1 = -1, p2 = -1;   // Position in the array

            // Attempt until a link is found
            boolean found = false;
            int iteration = 0;
            while (!found && iteration++ < MAX_ITERATION_LINK_SEARCH) {

                // Select the first from the still to link
                p1 = rand.nextInt(stillToLink.size());

                // Select the other from still to link (could be done deterministically)
                p2 = p1;
                while (p2 == p1) {
                    p2 = rand.nextInt(stillToLink.size());
                }

                // Convert into node indices
                int n1 = stillToLink.elementAt(p1);
                int n2 = stillToLink.elementAt(p2);

                // Only add n1-n2 link if it does not exist yet
                if (!this.isBidirNeighbor(n1, n2)) {
                    found = true;
                    addBidirNeighbor(n1, n2);
                }

            }

            // If a clique of nodes is left in the end, this makes it give up
            if (iteration >= MAX_ITERATION_LINK_SEARCH) {
                System.out.println("WARNING: Unable to find new pair for link between: " + stillToLink);
                stopSign = true;
            }

            // If it has not terminated yet, update the indices used for
            // the still to link vertices and the degrees used up
            if (!stopSign) {

                // Both for the vertices at position p1 and p2 a degree is used up
                // because a bi-directional edge was added
                degreeUsedUp.set(p1, degreeUsedUp.elementAt(p1) + 1);
                degreeUsedUp.set(p2, degreeUsedUp.elementAt(p2) + 1);

                // Check whether the vertex at p1 has exhausted all its network degrees
                boolean p1Deleted = false;
                if (degreeUsedUp.elementAt(p1) == networkDegree) {
                    stillToLink.remove(p1);
                    degreeUsedUp.remove(p1);
                    p1Deleted = true;
                }

                // Adjust position of p2 one back if p1 has been deleted in the vectors
                if (p1Deleted && p1 < p2) {
                    p2 = p2 - 1;
                }

                // Check whether the vertex at p2 has exhausted all its network degrees
                if (degreeUsedUp.elementAt(p2) == networkDegree) {
                    stillToLink.remove(p2);
                    degreeUsedUp.remove(p2);
                }

            }

            // If only a single vertex exists, there is no point in continuing the search
            if (stillToLink.size() == 1) {
                System.out.println("WARNING: Remaining just one node to link with degree " + degreeUsedUp.elementAt(0) + " out of " + networkDegree + ".");
                stopSign = true;
            }

        }

    }

}
