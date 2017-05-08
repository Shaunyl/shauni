package com.fil.shauni.mainframe.impl.cli;

import com.fil.shauni.log.LogLevel;
import com.fil.shauni.mainframe.ui.CommandLinePresentation;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

/**
 *
 * @author Shaunyl
 */
@Component @Configuration @ComponentScan(basePackages = {"com.fil.shauni"})
@ImportResource("file:src/main/resources/beans/Beans.xml") @Log4j2
public class CommandLine implements CommandLinePresentation {
    
    @Override
    public void print(LogLevel level, String msg, Object... parameters) {
        String message = String.format(msg, parameters);
        switch (level) {
            case INFO: log.info(message); break;
            case DEBUG: log.debug(message); break;
            case ERROR: log.error(message); break;
            default: log.warn(message);
        }
    }
    
    @Override
    public void printIf(boolean condition, LogLevel level, String msg, Object... parameters) {
        if (condition) {
            this.print(level, msg, parameters);
        }
    }
    
}
