package com.fil.shauni.command.support;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@FunctionalInterface
public interface Logger {
    void log(String message, Object... parameters);
}
