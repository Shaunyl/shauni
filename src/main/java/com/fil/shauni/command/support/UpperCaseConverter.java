package com.fil.shauni.command.support;

import com.beust.jcommander.converters.BaseConverter;

/**
 *
 * @author Filippo
 */
public class UpperCaseConverter extends BaseConverter<String> {

    public UpperCaseConverter(String option) {
        super(option);
    }

    @Override
    public String convert(String value) {
        return value.toUpperCase();
    }
}
