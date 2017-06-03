package com.fil.shauni.mainframe.impl.cli;

import com.fil.shauni.command.support.Logger;
import com.fil.shauni.log.LogLevel;
import com.fil.shauni.mainframe.ui.CommandLinePresentation;
import java.util.function.Supplier;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 *
 * @author Shaunyl
 */
@Log4j2 @Component
public class CommandLine implements CommandLinePresentation {

    @Override @Deprecated
    public void print(LogLevel level, String msg, Object... parameters) {
        String message = String.format(msg, parameters);
        switch (level) {
            case INFO:
                log.info(message);
                break;
            case DEBUG:
                log.debug(message);
                break;
            case ERROR:
                log.error(message);
                break;
            case WARN:
                log.warn(message);
                break;
            default:
                log.fatal(message);
        }
    }

    @Override @Deprecated
    public void printIf(boolean condition, LogLevel level, String msg, Object... parameters) {
        if (condition) {
            this.print(level, msg, parameters);
        }
    }

    @Override
    public void print(Logger logger, String message, Object... parameters) {
        logger.log(String.format(message, parameters), parameters);
    }

    @Override
    public void print(Supplier<Boolean> p, Logger logger, String message, Object... parameters) {
        if (p.get()) {
            this.print(logger, message, parameters);
        }
    }
}
