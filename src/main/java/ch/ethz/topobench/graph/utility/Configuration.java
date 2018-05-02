/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Configuration {

    private Map<String, String> config;

    public Configuration(String file) {
        this.config = new HashMap<String, String>();
        this.load(file);
    }

    /**
     * Load the configuration from the run.config file.
     *
     * @param file  File name
     */
    private void load(String file) {

        try {

            // Open file stream
            FileInputStream fileStream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));

            // Simply read in the server pairs
            String strLine;
            while ((strLine = br.readLine()) != null) {
                String[] parts = strLine.split(";");
                String[] kv = parts[0].split("=");
                this.config.put(kv[0], kv[1]);
            }

            // Close stream
            br.close();

        } catch (Exception e) {
            throw new RuntimeException("Failure to read configuration; " + e.getMessage());
        }

    }

    /**
     * Retrieve value for given key.
     *
     * @param key   Configuration key (e.g. python_version)
     *
     * @return Value (e.g. 2)
     */
    public String get(String key) {
        return this.config.get(key);
    }

}
