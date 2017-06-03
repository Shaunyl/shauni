package com.fil.shauni.command.support;

/**
 *
 * @author Filippo
 */
@FunctionalInterface
public interface Logger {
    void log(String message, Object... parameters);
}
