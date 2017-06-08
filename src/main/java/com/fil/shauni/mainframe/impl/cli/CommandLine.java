package com.fil.shauni.mainframe.impl.cli;

import com.fil.shauni.command.support.Logger;
import com.fil.shauni.mainframe.ui.CommandLinePresentation;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@Log4j2 @Component
public class CommandLine implements CommandLinePresentation {
    @Override
    public void print(Logger logger, String message, Object... parameters) {
        logger.log(String.format(message, parameters), parameters);
    }

    @Override
    public void print(boolean condition, Logger logger, String message, Object... parameters) {
        if (condition) {
            this.print(logger, message, parameters);
        }
    }
}
