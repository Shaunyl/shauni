package com.fil.shauni.util;

import java.time.format.DateTimeFormatter;

/**
 *
 * @author Filippo
 */
@FunctionalInterface
public interface DateFormatter {
    String format(DateTimeFormatter t);
}