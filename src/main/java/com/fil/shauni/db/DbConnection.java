package com.fil.shauni.db;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author Shaunyl
 */
@AllArgsConstructor
public class DbConnection {
    @Getter
    final String url, user, passwd, sid, host;
}
