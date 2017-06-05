package com.fil.shauni.command.parser;

import com.fil.shauni.command.Command;
import java.util.List;

/**
 *
 * @author Filippo
 */
public interface CommandParser {
    Class<? extends Command.CommandAction> parse(List<String> args);
}
