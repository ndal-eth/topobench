**Loose Ends**
* `printFlowOriginNode` in both `AnalyzeMFCLP` and `AnalyzeSimpleLPFlow` is targeted at a single traffic mode, respectively MAWP/MIWP and ATA. However, what is printed as flow from that origin node does not make sense / fails for other traffic modes. This should be made generic. E.g. `int n = ((int) Math.sqrt(tuples.size())) + 1;` in `AnalyzeSimpeLPFlow.printFlowOriginNode` only works for ATA.
* `FileBidirGraph` is old and should be improved to handle e.g. different node weights.
* Make `Configuration` more intelligent.
* Within `Graph`, make `shortestPathLen[]` completely handled by itself, in a similar fashion as done with the node weights.
* Consider renaming `Graph` to `BiDirGraph`, and adding the ability to create directed graphs (e.g. add `DirGraph`), abstracting `Graph` further.

**Testing**
* Achieve near-perfect test coverage.
* Write an end-to-end tests that run both modes in their entirety, automatically testing if the resulting objective value is correct for the given circumstances.
* Check platform compatibility with common versions of Windows, Mac OSX, and Linux.

**Graph Variety**
* Hypercube
* BCube
* DCell
* Dragonfly
* FlattenedButterfly
* VL2
* ... others

**New Features**
* Add mode for static topology bounds calculator (`ch.ethz.topobench.graph.utility.BoundsCalculator`).
* Random traffic mode?

**Too Ambitious**
* Multi-lingual support for the command line