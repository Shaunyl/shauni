package com.fil.shauni.command.export.support;

import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@Log4j2
public class Query extends Entity {

    private final static Pattern PATTERN = Pattern.compile("([^;].*?)(?:\\((.*?)\\))?:([^;]*)");

    public Query(String s) {
        super(s);
    }
    
    @Override
    public String convert(String s) {
        return convert(PATTERN.matcher(s), (i) -> String.format("SELECT %s FROM %s %s", (Object[]) i));
    }

    private String convert(Matcher m, Function<String[], String> op) {
        Predicate<Matcher> p = (o) -> o.find();
        if (p.test(m)) {
            return op.apply(new String[]{ m.group(2) == null ? "*" : m.group(2), m.group(1), m.group(3) });
        }
        log.error("Syntax not valid for a query");
        return null;
    }

    @Override
    public String display(String s) {
        Matcher m = PATTERN.matcher(s);
        if (m.find()) {
            return m.group(1).toUpperCase();
        }
        return null;
//        throw new ShauniException("not displayable."); // FIXME
    }
}
