package com.fil.shauni.util;

/**
 *
 * @author FTestino
 */
public class StringUtil {
    public static String replace(String source, String... args) {
        String result = source;
        for (int i = 0; i < args.length; i+=2) {
            result = source.replace(args[i], args[i+1]);
        }
        
        return result;
    }
}
