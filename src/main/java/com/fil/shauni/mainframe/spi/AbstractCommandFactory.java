package com.fil.shauni.mainframe.spi;

import com.fil.shauni.command.Command;

/**
 *
 * @author Chiara
 */
public interface AbstractCommandFactory {
    Command createDatabaseCommand(final String[] urls);
    Command createConfigurationCommand();
}
