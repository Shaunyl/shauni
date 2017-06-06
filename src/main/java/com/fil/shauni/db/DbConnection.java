package com.fil.shauni.db;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@AllArgsConstructor
public class DbConnection {
    @Getter
    final String url, user, passwd, sid, host;
}
