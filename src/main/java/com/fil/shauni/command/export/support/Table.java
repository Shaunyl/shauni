package com.fil.shauni.command.export.support;

import com.fil.shauni.concurrency.pool.FixedThreadPoolManager;
import com.fil.shauni.concurrency.pool.ThreadPoolManager;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@Log4j2
public class Table extends Entity {

    private final static Pattern PATTERN = Pattern.compile("(\\w+\\.\\w+)");

    public Table(String s) {
        super(s);
    }

    @Override
    public String convert(String s) {
        return convert(PATTERN.matcher(s), (i) -> String.format("SELECT * FROM %s", (Object[]) i));
    }

    private String convert(Matcher m, Function<String[], String> op) {
        Predicate<Matcher> p = (o) -> o.find();
        if (p.test(m)) {
            return op.apply(new String[]{ m.group(1) }).toUpperCase();
        }
        return null;
    }

    @Override
    public String display(String s) {
        Matcher m = PATTERN.matcher(s);
        if (m.find()) {
            return m.group(1).toUpperCase();
        }
        return null;
    }
}
