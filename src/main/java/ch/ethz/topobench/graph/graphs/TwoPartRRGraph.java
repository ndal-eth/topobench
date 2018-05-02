package ch.ethz.topobench.graph.graphs;

import ch.ethz.topobench.graph.Link;
import ch.ethz.topobench.Main;
import ch.ethz.topobench.graph.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * The two-part random regular graph consists out of two random regular graphs.
 * The A2A part has x% of all n nodes, the SUPP part has (1-x)% of all n nodes.
 *
 * The A2A graph is a generated RRG with degree r = i + e
 *
 * The SUPP graph is a generated RRG with degree r = i' + e'
 *
 */
public class TwoPartRRGraph extends Graph {

    public static void main(String[] args) {
        Main.initGlobalRandomness(0);
        new TwoPartRRGraph(5, 3, 100, 0.9, 1, 1);
    }

    /**
     * Constructor.
     *
     * @param r         Number of network ports per switch
     * @param k         Number of server ports per switch
     * @param n         Number of switches
     * @param fraction  Size of the A2A part of the graph
     * @param eA2A      Number of external network ports per node of the A2A part that is randomly
     *                  connected to the SUPP part
     * @param eSUPP     Number of external network ports per node of the SUPP part that is randomly
     *                  connected to the A2A part
     */
    public TwoPartRRGraph(int r, int k, int n, double fraction, int eA2A, int eSUPP) {
        super("2PartRRG", n, k);

        // Calculate (n, i, e) for A2A graph
        int nA2A = (int) (numNodes * fraction);
        int iA2A = r - eA2A; // Internal degree of the A2A part
        System.out.println("A2A graph: n: " + nA2A + ", internal degree: " + iA2A + ", external degree: " + eA2A);

        // Calculate (n, i, e) for SUPP graph
        int nSUPP = n - nA2A;
        int iSUPP = r - eSUPP;
        System.out.println("SUPP graph: n: " + nSUPP + ", internal degree: " + iSUPP + ", external degree: " + eSUPP);

        // Generate corresponding graphs
        Graph gA2A = new RandomRegularGraph(nA2A, iA2A + k, iA2A);
        Graph gSUPP = new RandomRegularGraph(nSUPP, iSUPP + k, iSUPP);

        // Add A2A part to the graph
        for (int i = 0; i < gA2A.getAdjacencyList().length; i++) {
            for (Link l : gA2A.getAdjacencyList()[i]) {
                if (i <= l.getLinkTo()) {
                    this.addBidirNeighbor(i, l.getLinkTo());
                }
            }
        }

        // Add SUPP part to the graph
        for (int i = 0; i < gSUPP.getAdjacencyList().length; i++) {
            for (Link l : gSUPP.getAdjacencyList()[i]) {
                if (i <= l.getLinkTo()) {
                    this.addBidirNeighbor(nA2A + i, nA2A + l.getLinkTo());
                }
            }
        }

        // As both are regular graphs, and by definition unfulfilled,
        // add all their indices to the remainder list
        List<Integer> remainderLeftList = new ArrayList<>();
        for (int i = 0; i < nA2A; i++) {
            for (int j = 0; j < eA2A; j++) { // Also allow the edge cases to randomly matched? Replace eA2A with r - adjacencyList[i].size()
                remainderLeftList.add(i);
            }
        }
        int leftStartSize = remainderLeftList.size();

        List<Integer> remainderRightList = new ArrayList<>();
        for (int i = nA2A; i < n; i++) {
            for (int j = 0; j < eSUPP; j++) { // Also allow the edge cases to randomly matched? Replace eSUPP with r - adjacencyList[i].size()
                remainderRightList.add(i);
            }
        }
        int rightStartSize = remainderRightList.size();

        // Add links between A2A and SUPP until either one is full
        while (remainderLeftList.size() != 0 && remainderRightList.size() != 0) {

            // Take two random open network ports from the A2A and SUPP graph
            int leftIdx = Main.universalRand.nextInt(remainderLeftList.size());
            int rightIdx = Main.universalRand.nextInt(remainderRightList.size());
            int leftNodeId = remainderLeftList.get(leftIdx);
            int rightNodeId = remainderRightList.get(rightIdx);

            // Add the link between the two
            this.addBidirNeighbor(leftNodeId, rightNodeId);

            // Remove from the options of open network ports
            remainderLeftList.remove(leftIdx);
            remainderRightList.remove(rightIdx);

        }

        System.out.println("Remainder open network ports A2A: " + remainderLeftList.size() + "/" + leftStartSize + ", remainder open network ports SUPP: " + remainderRightList.size() + "/" + rightStartSize);
        System.out.println("A2A and SUPP graph are merged into one.");

    }

}
