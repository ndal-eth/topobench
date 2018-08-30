# TopoBench

TopoBench is a framework to compare network topologies with each other. Topobench uses a fluid flow model, with perfect traffic engineering using a linear program which solves a maximum concurrent flow problem. Topobench is focused on benchmarking the network, and as such models traffic matrices which are not bottlenecked at servers. This code extends the original TopoBench release ([https://github.com/ankitsingla/topobench](https://github.com/ankitsingla/topobench)) and improves its usability.

The framework is parametrized with three main components:
1. **Graph:** any network topology with any configuration of links, link capacities, nodes and servers;
2. **Path Evaluator:** for a flow (s, t), this decides which of the links can be taken. This can be used to determine upper bounds on e.g. K-shortest paths, Valiant Load Balancing or all links;
3. **Traffic:** any traffic matrix defined at the server level, e.g. All-to-All, All-to-One, or Maximum Weight Pairs.

With these three component parameters, the framework calculates the following: given a graph G, a traffic matrix TM and flow restrictions from the Path Evaluator, what is the highest K for which the traffic matrix K*TM is satisfiable in graph G. Besides calculating this, it also offers an analysis tool to investigate e.g. bottlenecks.

## Getting Started

#### 1. Software dependencies

* **Java 8:** Version 8 of Java; both Oracle JDK and OpenJDK are supported and produce under that same seed deterministic results. Additionally the project uses the Apache Maven software project management and comprehension tool (version 3+).

* **Gurobi:** Our code uses the Gurobi solver (http://www.gurobi.com/) which is available for free to academics. It can be swapped with any linear program solver, although this requires significant effort in rewriting both scripts and java code (especially the analysis tools offered). After installation, please ensure that you can globally run it from the command line as `gurobi_cl <a linear program.lp>`. It is also possible to run it on a remote server by swapping `localLpRun.sh` for `remoteLpRun.sh` in your script (be sure to edit `remoteLpRun.sh` to your own setup).

* **Python:** Some functionality (e.g. maximum weight matching, Xpander graph generation) uses Python. Both Python 2 and 3 are supported. Be sure you can globally run `python <some python script.py>`, and have the modules *networkx-version-1.11* and *numpy* installed with your respective distribution. Particularly you must do: `sudo pip install networkx==1.11` because networkx has broken backwards in its 2.x release.

* **Gnuplot:** Some scripts have been written to nicely plot results. This is not a required dependency. Be sure you can globally run `gnuplot <some plot script.plt>`. Preferably version 4.4 or higher.

Although the software is designed for Linux, it is also possible to run it on Windows using Cygwin.

#### 2. Run Environment Configuration

1. Open `run.config.template` and save it as `run.config`;
2. Set the python version to `2` or `3` depending on your set-up;
3. Set the sort command to a Unix-compatible sort command. On Linux this is the normal `sort`, on Windows with Cygwin this is for example `C:/Cygwin/bin/sort` (using just `sort` on Windows will not work, as it has one of its own);
4. Set the K-upper bound to `25` (this is merely an optimization for linear program solver);
5. Set silent command to `1` (unless you want to see when and what type of commands are being executed, it's for debugging).

#### 3. Testing

1. Import the project as a Maven project into your favorite editor (IntelliJ is highly recommended);

2. Your editor should now be loading the dependencies (JUnit, Mockito, Apache Commons CLI);

3. Run all tests in the project, make sure that they all pass; this can be done using the following maven command: `mvn compile test`

4. Build the executable `TopoBench.jar` by using the following maven command: `mvn clean compile assembly:single`

5. Within `scripts/tutorial`, run `bash tutorial.sh`. It shows what it is doing in the terminal. If it produces exceptions, kill the process and investigate;

6. Within `plot/tutorial`, run `gnuplot figure_tutorial.plt`. It should generate a graph file in PDF format: `plot/tutorial/figure_tutorial.pdf`. The plot should look like a declining line.

## The Executable TopoBench.jar

This entire framework is built using maven into a single runnable jar `TopoBench.jar`. This executable takes arguments in a very specific order (the `\\` indicates the line continues).

#### Mode: Produce

```
java -jar TopoBench.jar \
-mode PRODUCE -seed $seed -lpt $lpt \
-gt $graph -pe $patheval -tm $tmode \
<graph parameters> \
<path evaluator parameters> \
<traffic parameters>
```

The parameters given should be as follows:

* **$seed:** any long integer, `0` for random. This guarantees repeatable runs.
* **$lpt:** linear program type, either `MCFFC` or `SIMPLE`. `MCFFC` works best for sparse traffic matrices (e.g. `RPP`, `MIWP`, `MAWP`), whereas `SIMPLE` works best for dense traffic matrices (e.g. `ATA`).
* **$graph:** any graph topology, e.g. `JF`, `FT`, or `XP`
* **$patheval:** any path evaluator, e.g. `SLACK`, `KSHRT`, or `VALIA`
* **$tmode:** any traffic mode, e.g. `A2A`, `MAWP`, or `RPP`
* **\<graph parameters\>:** parameters for the topology (e.g. see `ch.ethz.topobench.graph.graphs.generators.XpanderGraphGenerator`). It takes linearly parameters until it arrives at one it does not know. It then passes the remainder to the path evaluator.
* **\<path evaluator parameters\>:** parameters for the path evaluator (e.g. see `ch.ethz.topobench.graph.patheval.generators.SlackPathEvaluator`). It takes parameters linearly until it arrives at one it does not know. It then passes the remainder to the traffic.
* **\<traffic parameters\>:** parameters for the traffic (e.g. see `ch.ethz.topobench.graph.traffic.generators.RandomPermPairsTrafficGenerator`). All remaining parameters must be destined for the traffic, else the executable fails.

#### Mode: Analyze

```
java -jar TopoBench.jar \
-mode ANALYZE -seed $seed -lpt $lpt \
-tm $tmode
```

The parameters given should be as follows:

* **$seed:** the seed that was used in the PRODUCE run. This guarantees repeatable runs.

* **$lpt:** linear program type that was used in the PRODUCE run, either `MCFFC` or `SIMPLE`.

* **$tmode:** the traffic mode that was used in the PRODUCE run, e.g. `A2A`, `MAWP`, or `RPP`

## Extending TopoBench

The `*` is a placeholder for the `ethz.ch.topobench` package in the upcoming sections. TopoBench can be easily extended with a new graph, path evaluator or traffic mode.

#### Adding a new Graph

1. Let X be the graph name
2. Write a new class `*.graph.graphs.XGraph` extending `*.topobench.graph.Graph`
3. Write a new generator `*.graph.graphs.generators.XGraphGenerator` implementing `*.graph.graphs.generators.GraphGenerator` that parses the command line arguments
4. Add it to the `Type` enumerator, and the switches in `getGraphTypeRepresentation`, `getGraphType` and `select` methods of `*.graph.graphs.GraphSelector`

#### Adding a new Path Evaluator

1. Let X be the path evaluator name
2. Write a new class `*.graph.patheval.XPathEvaluator` extending `*.graph.patheval.PathEvaluator`
3. Write a new generator `*.graph.patheval.generators.XPathEvaluatorGenerator` implementing `*.graph.patheval.generators.PathEvaluatorGenerator` that parses the command line arguments
4. Add it to the `Type` enumerator, and the switches in `getPathEvaluatorRepresentation`, `getPathEvaluator` and `select` methods of `*.graph.patheval.PathEvaluatorSelector`

#### Adding a new Traffic Mode

1. Let X be the traffic mode name
2. Write a new class `*.graph.traffic.XTraffic` extending `*.graph.traffic.Traffic`
3. Write a new generator `*.graph.traffic.generators.XTrafficGenerator` implementing `*.graph.traffic.generators.TrafficGenerator` that parses the command line arguments
4. Add it to the `Type` enumerator, and the switches in `getTrafficModeRepresentation`, `getTrafficMode` and `select` methods of `*.graph.traffic.TrafficSelector`

**Limitations to TMs:** the traffic modes generate a server-level traffic matrix. It is only possible to define a pair (*svr_source*, *svr_target*) if *svr_source* and *svr_target* are *not* on the same switch. This is because the linear program operates at a switch level granularity. Failure to comply to this in your own traffic mode results in an `IllegalArgumentException` being thrown by the linear program writer.

## Organization

#### Contributors
* Ankit Singla (ankit.singla (at) ethz [dot] ch)
* Simon Kassing (kassings (at) ethz [dot] ch)
* Sangeetha Abdu Jyothi (abdujyo2 (at) illinois [dot] edu)
* Chi-Yao Hong
* Lucian Popa
* P. Brighten Godfrey
* Alexandra Kolla

#### License and Usage
The `empirical-graphs` directory contains a collection of network topologies, which are not our work. The sources for these are cited in `empirical-graphs/references.pdf`.

The code in the `src/main/java/edu/asu/emit/algorithm` directory and all directories beneath are adapted from [Yan-Qi's Github Repository](https://github.com/yan-qi/k-shortest-paths-java-version)  and is licensed under the Arizona State University (ASU) license. Every source file that falls under the ASU license contains a copy of that license.

The Python Xpander generation code (`python/xpanderGen2.py` and `python/xpanderGen3.py`) are adapted from source code directly provided its authors. The Xpander topology originates from the paper *"Asaf Valadarsky, Gal Shahaf, Michael Dinitz, and Michael Schapira. Xpander: Towards Optimal-Performance Datacenters. ACM CoNEXT 2016".*

All other code / scripts / materials are original contributions of the above contributors, and are released under the MIT LICENSE (see `LICENSE`).

We would appreciate you citing this code and the most relevant of our associated research publications below.

#### Background

The following three research papers (with their BibTex entries) explain the ideas behind this tool:

(1) *Jellyfish: Networking Data Centers Randomly (Ankit Singla, Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey), 2012*

```LaTeX
@inproceedings{singla2012jellyfish,
  title={{Jellyfish: Networking Data Centers Randomly}},
  author={Singla, Ankit and Hong, Chi-Yao and Popa, Lucian and Godfrey, P Brighten},
  booktitle={{9th USENIX Symposium on Networked Systems Design and Implementation}},
  year={2012}
}
```

(2) *High Throughput Data Center Topology Design (Ankit Singla, P. Brighten Godfrey, Alexandra Kolla), 2014*

```LaTeX
@inproceedings{singla2014heterogeneity,
  title={{High Throughput Data Center Topology Design}},
  author={Singla, Ankit and Godfrey, P Brighten and Kolla, Alexandra},
  booktitle={{11th USENIX Symposium on Networked Systems Design and Implementation}},
  year={2014}
}
```

(3) *Measuring and Understanding Throughput of Network Topologies (Sangeetha Abdu Jyothi, Ankit Singla, P. Brighten Godfrey, Alexandra Kolla), 2016*

```LaTeX
@inproceedings{abduJyothi2016,
  author = {Sangeetha Abdu Jyothi and Ankit Singla and P. Brighten Godfrey and Alexandra Kolla},
  title = {{Measuring and Understanding Throughput of Network Topologies}},
  booktitle={{29th ACM International Conference for High Performance Computing, Networking, Storage and Analysis}},
  year = {2016}
}
```

(4) *Fat-Free Topologies (Ankit Singla), 2016*

```LaTeX
@inproceedings{singla2016,
  author = {Ankit Singla},
  title = {{Fat-Free Topologies}},
  booktitle={{15th ACM Workshop on Hot Topics in Networks}},
  year = {2016}
}
```

(1) establishes the case for using random graphs as data center topologies. (2) further shows that not only do random graphs achieve higher throughput than traditional topologies, they are actually close to optimal. (2) also extends our results to heterogeneous topology design, where switches might not all have the same number of ports or the same line-speeds throughput. (3) explains the rationale for our use of the throughput metric and compares a large number of networks for throughput. (4) explores topology design in the context of skewed traffic matrices.

#### Notes

* Error handling is poor at this time. For example, if you're running networks that are too large, there will be memory errors etc. These have not been gracefully handled so far.

* In order to support scalability under all-to-all traffic, a simplified version of the LP has been added. While both the LPs yield the same result, the new version runs faster under dense traffic matrices such as all-to-all. Please note that the results in the papers were computed using the old version.

* This README, our papers mentioned above, and the comments in the code are the entirety of the documentation available. You can reach us at the email addresses mentioned above for other queries. Help is not promised, but if we have time, we can look into it.
