package com.fil.shauni.command.parser;

import com.fil.shauni.command.Command;
import com.fil.shauni.command.Command;
import com.fil.shauni.command.CommandLineSupporter;
import com.fil.shauni.command.CommandLineSupporter;

/**
 *
 * @author Filippo
 */
public interface CommandParser {
    Class<? extends Command> parse(CommandLineSupporter s, String[] args);
}
