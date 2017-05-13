package com.fil.shauni.command.support;

import com.beust.jcommander.converters.IParameterSplitter;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Shaunyl
 */
public class SemicolonParameterSplitter implements IParameterSplitter {

    @Override
    public List<String> split(String value) {
        return Arrays.asList(value.split(";"));
    }
}
