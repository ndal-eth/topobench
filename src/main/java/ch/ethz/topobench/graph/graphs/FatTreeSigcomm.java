/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.graphs;

import ch.ethz.topobench.graph.Graph;

/**
 * FAT-tree graph topology implemented based on the following paper at the SIGCOMM '08 conference:
 *
 * "A Scalable, Commodity Data Center Network Architecture"
 * by Mohammad Al-Fares, Alexander Loukissas, and Amin Vahdat (2008).
 *
 * No source code in this class was provided by the paper's authors.
 *
 * The topology of three layers (total nodes are 5/4*K*K):
 *
 * 1) Bottom with index  0     -  K*K/2
 * 2) Middle with index  K*K/2 -  K*K
 * 3) Core with index    K*K   -  5/4*K*K
 *
 * For example (K = 4):
 *
 *        16       17        18        19                   c-nodes
 *
 * < Each m-node is connected to K/2 core layer nodes >
 *
 *      8   9   10    11  12    13  14    15                m-nodes
 *      | X |   |  X  |   |  X  |   |  X  |
 *      0   1   2     3   4     5   6     7                 l-nodes
 *
 */
public class FatTreeSigcomm extends Graph {
	
	private int K;

	public FatTreeSigcomm(int K){
		super("fat", K * K * 5/4, 0);
		this.K = K;
		populateAdjacencyList();
        System.out.println("Constructed Fat-Tree with K = " + K + " (switches: " + this.getNumNodes() + ")");
	}

    /**
     * Create a fat tree knowing how much of servers ('hosts') it must have.
     *
     * @param hosts Amount of hosts
     *
     * @return Initialized FAT-tree with lowest possible K
     */
    public static FatTreeSigcomm createFatTreeByHosts(int hosts){

        // Determine K from the number of hosts it should hold
        double KExact = (int) Math.pow(hosts * 4, 1.0 / 3.0);
        int K = (int) KExact;
        if (K % 2 != 0){
            K++;
        }

        // Print rounding
        System.out.println("K would be " + KExact + " rounding to " + K + " leading to " + (K*K*K/4) + " hosts (wanted to have originally " + hosts + " hosts)");

        return new FatTreeSigcomm(K);

    }

    /**
     * Populate the adjacency list with a full FAT-tree.
     */
	private void populateAdjacencyList(){

		// Connect lower to middle (total edges added: K*K*K/4
		for (int pod = 0; pod < K; pod++) {
			for (int low = 0; low < K/2; low++) {
				for (int mid = 0; mid < K/2; mid++) {
					addBidirNeighbor(pod * K/2 + low, K*K/2 + pod * K/2 + mid);
				}
			}
		}

		// Connect middle to core (total edges added: K*K*K/4
        for (int pod = 0; pod < K; pod++) { // Go over every pod
            for (int inPod = 0; inPod < K/2; inPod++) { // Each of the node in pod
                for (int core = 0; core < K/2; core++) { // Connects to K/2 cores (offset by inPod*K/2)
                    addBidirNeighbor(K*K/2 + pod * K/2 + inPod, K*K + inPod*K/2 + core);
                }
            }
        }

		// Set weight non-zero (K/2) only for leaf nodes
		for (int pod = 0; pod < K; pod++) {
			for (int i = 0; i < K/2; i++){
				this.setNodeWeight(pod * K/2 + i, K/2);
			}
		}

	}

    /**
     * Get parameter K.
     *
     * @return Parameter K value
     */
	public int getK(){
		return K;
	}

}
