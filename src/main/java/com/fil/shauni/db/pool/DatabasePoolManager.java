package com.fil.shauni.db.pool;

import java.sql.Connection;

/**
 *
 * @author Shaunyl
 */
public interface DatabasePoolManager {
    void configure(String url, String user, String passwd, String host, String sid, int poolsize);
    void configure(String url, String user, String passwd, String host, String sid);
    Connection getConnection();
    void closeConnection(Connection conn);
    void close();
    String getSid();
    String getHost();
}
