package com.fil.shauni.mainframe.spi;

import com.fil.shauni.command.Command;
import lombok.NonNull;

/**
 *
 * @author Chiara
 */
public interface AbstractCommandFactory {
    Command createDatabaseCommand(final String[] urls);
    Command createConfigurationCommand();
        
    Command springExporter(final @NonNull String format);
}
