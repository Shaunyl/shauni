package com.fil.shauni.util;

import java.time.format.DateTimeFormatter;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@FunctionalInterface
public interface DateFormatter {
    String format(DateTimeFormatter t);
}