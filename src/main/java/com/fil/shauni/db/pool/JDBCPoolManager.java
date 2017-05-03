package com.fil.shauni.db.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

/**
 *
 * @author Shaunyl
 * @version v0.1 basic hardcoded pool
 */
@Log4j2 // @Component
public class JDBCPoolManager implements DatabasePoolManager {

    private BasicDataSource ds;
    
    private static final int POOL_SIZE = 5;
    
    @Override
    public void configure(String url, String user, String passwd, String host, String sid) {
//        this.url = url;
//        this.user = user;
//        this.passwd = passwd;
        this.host = host;
        this.sid = sid;
        this.configure(url, user, passwd, host, sid, POOL_SIZE);
    }
    
    private String host, sid;
    
    @Override
    public void configure(String url, String user, String passwd, String host, String sid, int poolsize) {

        try {
            Properties properties = new Properties();

            properties.put("driverClassName", "oracle.jdbc.driver.OracleDriver");
            properties.put("url", url);

            properties.put("user", user);
            properties.put("password", passwd);
            if ("sys".equals(user.toLowerCase())) {
                properties.put("internal_logon", "sysdba");
            }

            properties.put("defaultAutoCommit", String.valueOf(Boolean.FALSE));
            properties.put("maxActive", String.valueOf(poolsize));

            StringBuilder connectionProperties = new StringBuilder();
            for (Iterator iter = properties.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                connectionProperties.append(key).append('=').append(value);
                if (iter.hasNext()) {
                    connectionProperties.append(';');
                }
            }
            properties.put("connectionProperties", connectionProperties.toString());

            ds = null;
            ds = (BasicDataSource) BasicDataSourceFactory.createDataSource(properties);

        } catch (Exception e) {
            String message = "Could not create a connection pool: " + e;
            if (ds != null) {
                try {
                    ds.close();
                } catch (Exception e2) {
                    // ignore
                }
                ds = null;
            }
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public Connection getConnection() {
        Connection conn = null;
        try {
            if (ds == null) {
                log.error("Data Source is null.");
            }
            conn = ds.getConnection();
            conn.setAutoCommit(false); //FIXME: hardcoded.
        } catch(SQLException e) {
            log.error("Connection pool exception\n  ->  {}", e.getMessage());
        } 
        finally {
        }
        return conn;
    }

    @Override
    public void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException ex) {
            log.error("Error while closing connection\n  -> {}", ex.getMessage());
            throw new RuntimeException();
        } finally {
        }
    }

    @Override
    public void close() throws RuntimeException {
        try {
            if (ds != null) {
                ds.close();
                ds = null;
            } else {
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not close DBCP pool", e);
        }
    }

    @Override
    public String getSid() {
        return sid;
    }

    @Override
    public String getHost() {
        return host;
    }
}