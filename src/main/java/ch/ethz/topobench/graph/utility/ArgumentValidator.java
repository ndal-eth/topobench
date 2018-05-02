/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.utility;

public class ArgumentValidator {

    public static int retrieveInteger(String argName, String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new RuntimeException("FATAL: argument " + argName + " is not a valid integer (received: " + s + ").");
        }
    }

    public static double retrieveDouble(String argName, String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new RuntimeException("FATAL: argument " + argName + " is not a valid double.");
        }
    }

    public static long retrieveLong(String argName, String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new RuntimeException("FATAL: argument " + argName + " is not a valid long.");
        }
    }

    public static boolean retrieveBoolean(String argName, String s) {
        try {
            int i = Integer.parseInt(s);
            return i == 1;
        } catch (NumberFormatException e) {
            throw new RuntimeException("FATAL: argument " + argName + " is not a valid boolean (0 or 1).");
        }
    }

}
