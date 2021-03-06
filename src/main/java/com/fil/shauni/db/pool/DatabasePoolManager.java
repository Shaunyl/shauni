package com.fil.shauni.db.pool;

import java.sql.Connection;
import javax.sql.DataSource;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
public interface DatabasePoolManager extends AutoCloseable {
    DataSource getDataSource();

    void configure(String url, String user, String passwd, String host, String sid, int poolsize);
    void configure(String url, String user, String passwd, String host, String sid);
    Connection getConnection();
    void closeConnection(Connection conn);
    String getSid();
    String getHost();
    String getUsername();
}
