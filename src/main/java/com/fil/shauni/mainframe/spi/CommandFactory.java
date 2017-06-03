package com.fil.shauni.mainframe.spi;

import com.fil.shauni.Main;
import com.fil.shauni.command.Command;
import com.fil.shauni.command.ConfigCommandControl;
import com.fil.shauni.command.DatabaseCommandControl;
import lombok.NonNull;

/**
 *
 * @author Chiara
 */
public class CommandFactory implements AbstractCommandFactory {

    private final String subcmd;

    private final Class<? extends Command> clazz;

    public CommandFactory(String subcmd, Class<? extends Command> clazz) {
        this.subcmd = subcmd;
        this.clazz = clazz;
    }

    @Override
    public DatabaseCommandControl createDatabaseCommand(String[] urls) {
        Command cmd = Main.beanFactory.getBean(subcmd, clazz);
        final DatabaseCommandControl dbc = (DatabaseCommandControl) cmd;
        dbc.setConnections(urls);
        return dbc;
    }

    @Override
    public ConfigCommandControl createConfigurationCommand() {
        Command cmd = Main.beanFactory.getBean(subcmd, clazz);
        return (ConfigCommandControl) cmd;
    }

    @Override
    public Command springExporter(final @NonNull String format) {
        return (Command) Main.beanFactory.getBean("springExporter", format);
    }
}
