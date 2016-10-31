/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.print;

import ch.ethz.topobench.graph.traffic.TrafficPair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class PrinterTrafficPairs {

    /**
     * Print the traffic pairs.
     *
     * @param fileName    Destination file
     */
    public static void print(String fileName, List<TrafficPair> pairs) {

        try {

            // Open output stream
            FileWriter fileStream = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(fileStream);

            for (TrafficPair pair : pairs) {
                out.write(pair.getFrom() + " " + pair.getTo() + "\n");
            }

            // Close output stream
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
