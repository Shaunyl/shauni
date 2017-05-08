package com.fil.shauni.util;

/**
 *
 * @author FTestino
 */
public class StringUtil {
    public static String replace(String source, String... args) {
        for (int i = 0; i < args.length; i+=2) {
            source = source.replace(args[i], args[i+1]);
        }
        
        return source;
    }
}
