package com.fil.shauni.mainframe.ui;

import com.fil.shauni.command.support.Logger;
import com.fil.shauni.log.LogLevel;
import java.util.function.Supplier;

/**
 *
 * @author Shaunyl
 */
public interface CommandLinePresentation {

    /**
     * Print a message
     *
     * @param level the log level (info, debug, error, warn)
     * @param msg the message to be printed
     * @param parameters wildcard replacements
     */
    void print(LogLevel level, String msg, Object... parameters);

    /**
     * Print a message if the condition is true
     *
     * @param condition the condition to check for
     * @param level the log level (info, debug, error, warn)
     * @param msg the message to be printed
     * @param parameters wildcard replacements
     */
    void printIf(boolean condition, LogLevel level, String msg, Object... parameters);

    void print(Logger logger, String message, Object... parameters);

    void print(Supplier<Boolean> p, Logger logger, String message, Object... parameters);
}
