package com.fil.shauni.command.support;

import com.beust.jcommander.converters.BaseConverter;

/**
 *
 * @author Filippo
 */
public class CharConverter extends BaseConverter<Character> {

    public CharConverter(String option) {
        super(option);
    }

    @Override
    public Character convert(String value) {
        return value.charAt(0);
    }

}
