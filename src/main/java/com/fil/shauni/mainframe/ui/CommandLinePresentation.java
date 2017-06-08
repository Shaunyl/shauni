package com.fil.shauni.mainframe.ui;

import com.fil.shauni.command.support.Logger;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
public interface CommandLinePresentation {
    void print(Logger logger, String message, Object... parameters);

    void print(boolean condition, Logger logger, String message, Object... parameters);
}
