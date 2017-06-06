package com.fil.shauni.util;

import lombok.NonNull;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
public class StringUtils {

    public static String replace(String source, String... args) {
        for (int i = 0; i < args.length; i += 2) {
            source = source.replace(args[i], args[i + 1]);
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

    public static String substr(final @NonNull String s, int maxLength) {
        int length = s.length();
        String str = s.substring(0, Math.min(maxLength, length));
        String notFitting = "";
        if (length > maxLength) {
            notFitting = "..";
        }
        return String.format("[%s%s]", str, notFitting);
    }
}
