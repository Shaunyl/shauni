package com.fil.shauni.command.export.support;

import com.fil.shauni.command.support.Exportable;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Filippo
 */
public class DatabaseTable implements Exportable {

    private final static Pattern pattern  = Pattern.compile("(\\w+\\.\\w+)");

    @Override
    public String convert(String s) {
        return convert(pattern.matcher(s), (i) -> String.format("SELECT * FROM %s", (Object[]) i));
    }

    private String convert(Matcher m, Function<String[], String> op) {
        Predicate<Matcher> p = (o) -> o.find();
        if (p.test(m)) {
            return op.apply(new String[]{ m.group(1) }).toUpperCase();
        }
        throw new RuntimeException("syntax not valid for query.");
    }
    
    @Override
    public String display(String s) {
        Matcher m = pattern.matcher(s);
        if (m.find()) {
            return m.group(1).toUpperCase();
        }
        throw new RuntimeException("not displayable.");
    }
}
