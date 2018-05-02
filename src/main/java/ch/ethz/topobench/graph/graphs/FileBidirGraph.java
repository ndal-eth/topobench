package ch.ethz.topobench.graph.graphs;

import ch.ethz.topobench.graph.Graph;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class FileBidirGraph extends Graph {

    /**
     * Create a bi-directional graph from file.
     *
     * The file for the m bi-directional edges *must* contain
     * either (a) 2m directed edges, or (b) m bi-directional edges.
     * Edges must be zero-indexed.
     *
     * E.g. for n=3 and 1=2=3 graph structure:
     *
     * 1 2
     * 2 3
     *
     * Or:
     *
     * 1 2
     * 2 1
     * 2 3
     * 3 2
     *
     * TODO: This is out of date, for example it should be possible to
     * TODO: to specify node weights. Also to read the n from file.
     *
     * @param n             Number of nodes in the graph
     * @param partSwitches  Participating switches, implying interval of identifiers [0, partSwitches)
     *                      Each receives a single server
     * @param filename      File name
     */
    public FileBidirGraph(int n, int partSwitches, String filename) {
        super("file", n, 0);

        try {

            // Open input stream
            FileInputStream fileStream = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));

            // Simply read in the server pairs
            String strLine;
            int doubles = 0;
            int edges = 0;
            while ((strLine = br.readLine()) != null) {

                // Split line
                String[] match = strLine.split(" ");
                int n1 = Integer.valueOf(match[0]);
                int n2 = Integer.valueOf(match[1]);

                // Check bounds
                if (n1 < 0 || n2 < 0 || n1 >= n || n2 >= n) {
                    throw new RuntimeException("Out of bounds link indexes (n=" + n + "), link: " + n1 + " - " + n2);
                }

                if (!isBidirNeighbor(n1, n2)) {
                    addBidirNeighbor(n1, n2);
                    doubles++;
                }

                edges++;

            }

            if (edges / 2 != doubles && edges != doubles) {
                throw new RuntimeException("FileGraph: Not a bi-directional graph specified; number of specified edges not equal to m or 2m.");
            }

            // Close input stream
            br.close();

            // Set weight non-zero only for leaf nodes
            for (int i = 0; i < partSwitches; i++) {
                this.setNodeWeight(i, 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("FileGraph: I/O exception thrown, graph could not be generated.");
        }

    }

}
