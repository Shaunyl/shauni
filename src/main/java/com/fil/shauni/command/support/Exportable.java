package com.fil.shauni.command.support;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
public interface Exportable extends Convertable, Displayable {
    default <T> T export(String obj, JdbcTemplate jdbc, ResultSetExtractor<T> rse) {
        return jdbc.query(obj, rse);
    }
}
