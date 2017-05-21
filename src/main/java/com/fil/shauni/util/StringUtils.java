package com.fil.shauni.util;

/**
 *
 * @author FTestino
 */
public class StringUtils {
    public static String replace(String source, String... args) {
        for (int i = 0; i < args.length; i+=2) {
            source = source.replace(args[i], args[i+1]);
        }
        
        return source;
    }
    
    public static String repeat(String str, int repeat) {
        String repeated = "";
        for (int i = 0; i < repeat; i++) {
            repeated += str;
        }
        return repeated;
    }
}
