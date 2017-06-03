package com.fil.shauni;

import com.fil.shauni.io.PropertiesFileManager;
import java.util.Map;

/**
 *
 * @author Filippo
 */
public class Project {

    public final static String MAIN_CFG_PATH = "shauni.properties";
    
    public final static String ROOT_DIRECTORY = System.getProperty("user.dir").replace("\\","/");;
    
    public final static String FILE_SEPARATOR = System.getProperty("file.separator");
    
    private final static Map<String, String> configuration = new PropertiesFileManager().readAllWithKeys(MAIN_CFG_PATH, "");

    public static String getProperty(String key) {
        return configuration.get(key);
    }
}
