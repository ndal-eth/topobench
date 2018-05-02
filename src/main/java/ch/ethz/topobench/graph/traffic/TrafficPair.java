/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.traffic;

/**
 * A traffic pair is essentially a directional pair of servers
 * which is used to populate the switch-level traffic matrix in the LP.
 */
public class TrafficPair {

    private final int from;
    private final int to;

    TrafficPair(int from, int to) {

        if (from < 0 || to < 0) {
            throw new IllegalArgumentException("Cannot have negative indices for traffic pairs (given: " + from + "-" + to + ")");
        }

        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof TrafficPair)) {
            return false;
        }
        final TrafficPair other = (TrafficPair) obj;
        return this.from == other.from && this.to == other.to;
    }


    @Override
    public int hashCode() {
        return 5000 * this.from + this.to; // TODO: Larger minimum hash? Like 10000?
    }

    public int getTo() {
        return to;
    }

    public int getFrom() {
        return from;
    }

}
