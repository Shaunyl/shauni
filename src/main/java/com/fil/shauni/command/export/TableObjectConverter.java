package com.fil.shauni.command.export;

import com.beust.jcommander.IStringConverter;

/**
 *
 * @author Filippo
 */
public class TableObjectConverter implements IStringConverter<ExporterTableObject> {

    @Override
    public ExporterTableObject convert(String string) {
        return new ExporterTableObject(string.toUpperCase());
    }
}
