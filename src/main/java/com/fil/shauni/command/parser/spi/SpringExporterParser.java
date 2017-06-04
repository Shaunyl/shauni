package com.fil.shauni.command.parser.spi;

import com.fil.shauni.command.Command;
import com.fil.shauni.command.Command;
import com.fil.shauni.command.CommandLineSupporter;
import com.fil.shauni.command.CommandLineSupporter;
import com.fil.shauni.command.export.spi.CSVExporter;
import com.fil.shauni.command.export.spi.TabularExporter;
import com.fil.shauni.command.parser.CommandParser;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Filippo
 */
@Log4j2
public class SpringExporterParser implements CommandParser {
    @Override
    public Class<? extends Command> parse(CommandLineSupporter s, String[] args) {
        String format = s.getValue("format", String.class, "tab");
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
