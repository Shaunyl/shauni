package com.fil.shauni.command.export;

import com.beust.jcommander.IStringConverter;

/**
 *
 * @author Filippo
 */
public class QueryObjectConverter implements IStringConverter<ExporterQueryObject> {
    @Override
    public ExporterQueryObject convert(String string) {
        return new ExporterQueryObject(string.toUpperCase());
    }
}
