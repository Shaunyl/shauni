package com.fil.shauni.command.parser.spi;

import com.fil.shauni.command.Command;
import static com.fil.shauni.command.CommandLineSupporter.*;
import com.fil.shauni.command.export.spi.CSVExporter;
import com.fil.shauni.command.export.spi.TabularExporter;
import com.fil.shauni.command.parser.CommandParser;
import java.util.List;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Filippo
 */
@Log4j2
public class SpringExporterParser implements CommandParser {
    @Override
    public Class<? extends Command.CommandAction> parse(List<String> args) {
        String format = getValue(args, "format", String.class, "tab");
        switch (format) {
            case "tab":
                return TabularExporter.class;
            case "csv":
                return CSVExporter.class;
            default:
                throw new RuntimeException("Format " + format + " not supported.");
        }
    }
}
