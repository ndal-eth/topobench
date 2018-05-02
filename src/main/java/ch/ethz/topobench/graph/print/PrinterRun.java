/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.print;

import ch.ethz.topobench.Main;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class PrinterRun {

    /**
     * Print the arguments of the run to a file.
     *
     * @param fileName  File name
     * @param args      Arguments
     */
    public static void print(String fileName, String[] args) {

        try {

            // Open output stream
            FileWriter fileStream = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(fileStream);

            for (int i = 0; i < args.length; i += 2) {
                out.write(args[i] + " " + args[i + 1] + "\n");
            }
            out.write("-seed " + Main.getRandomSeed());

            // Close output stream
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
