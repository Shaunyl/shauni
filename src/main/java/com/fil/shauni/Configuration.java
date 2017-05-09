package com.fil.shauni;

import com.fil.shauni.io.PropertiesFileManager;
import java.util.Map;

/**
 *
 * @author Chiara
 */
public class Configuration {

    public final static String MAIN_CFG_PATH = "shauni.properties";
    
    private final static Map<String, String> configuration = new PropertiesFileManager().readAllWithKeys(MAIN_CFG_PATH, "");

    public static String getProperty(String key) {
        return configuration.get(key);
    }
}
