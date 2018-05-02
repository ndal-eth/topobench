/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.analysis;

import java.io.*;

public class AnalyzeObjective {

    /**
     * Analyze the objective value, write results to temp/final/objective.txt
     */
    public static void printAnalysis(String targetFile) {

        try {

            // Open output stream
            FileWriter fileStreamOut = new FileWriter(targetFile);
            BufferedWriter out = new BufferedWriter(fileStreamOut);

            // Open input stream
            FileInputStream fileStream = new FileInputStream("temp/vector.sol");
            BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));

            // Simply read in the server pairs
            String strLine;
            while ((strLine = br.readLine()) != null) {

                // Structure of line must be: f_i_j flow
                if (!strLine.startsWith("K")) {
                    continue;
                }
                strLine = strLine.replaceAll("K ", "");

                // Split up sentences
                out.write(String.valueOf(Double.parseDouble(strLine)));

                break;
            }

            // Close input stream
            br.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
