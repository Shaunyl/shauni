package com.fil.shauni.command.export;

/**
 *
 * @author Filippo
 */
@FunctionalInterface
public interface Logger {
    void log(String message, Object... parameters);
}
