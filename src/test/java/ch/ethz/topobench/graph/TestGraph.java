/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph;

public class TestGraph extends Graph {

    public TestGraph(String name, int size) {
        super(name, size);
    }

    public TestGraph(String name, int size, int uniformWeight) {
        super(name, size, uniformWeight);
    }

    public boolean addBidirNeighbor(int n1, int n2) {
        return super.addBidirNeighbor(n1, n2);
    }

    public void setNodeWeight(int i, int weight) {
        super.setNodeWeight(i, weight);
    }

}
