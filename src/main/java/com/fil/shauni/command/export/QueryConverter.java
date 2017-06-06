package com.fil.shauni.command.export;

import com.beust.jcommander.IStringConverter;
import com.fil.shauni.command.export.support.Entity;
import com.fil.shauni.command.export.support.Query;

/**
 *
 * @author Filippo
 */
public class QueryConverter implements IStringConverter<Entity> {
    @Override
    public Entity convert(String value) {
        return new Query(value);
    }
}
