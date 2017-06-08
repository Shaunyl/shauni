package com.fil.shauni.command.parser.spi;

import com.fil.shauni.command.Command;
import static com.fil.shauni.command.CommandLineSupporter.*;
import com.fil.shauni.command.montbs.spi.AutoMonTbs;
import com.fil.shauni.command.montbs.spi.SimpleMonTbs;
import com.fil.shauni.command.parser.CommandParser;
import java.util.List;

/**
 *
 * @author Filippo
 */
public class DefaultMonTbsParser implements CommandParser {

    @Override
    public Class<? extends Command.CommandAction> parse(List<String> args) {
        boolean auto = isThere(args, "auto");
        if (auto) {
            return AutoMonTbs.class;
        } else {
            return SimpleMonTbs.class;
        }
    }
}
