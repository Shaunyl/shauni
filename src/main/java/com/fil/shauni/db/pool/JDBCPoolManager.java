package com.fil.shauni.db.pool;

import com.fil.shauni.io.spi.PropertiesFileManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import oracle.jdbc.OracleConnection;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

/**
 *
 * @author Shaunyl
 * @version v0.1 basic hardcoded pool
 */
@Log4j2
public class JDBCPoolManager implements DatabasePoolManager {

    @Getter
    private BasicDataSource dataSource;

    private static final int POOL_SIZE = 5;
    
    @Inject
    private PropertiesFileManager propertiesFileManager;

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

    private String timeout = null;

    @Override
    public void configure(String url, String user, String passwd, String host, String sid, int poolsize) {

        try {
            Properties properties = new Properties();

            // should read the driver from the shauni.properties
            properties.put("driverClassName", "oracle.jdbc.driver.OracleDriver");
            properties.put("url", url);

            properties.put("user", user);
            properties.put("password", passwd);
            if ("sys".equals(user.toLowerCase())) {
                properties.put("internal_logon", "sysdba");
            }

            properties.put("defaultAutoCommit", String.valueOf(Boolean.FALSE));
            properties.put("maxActive", String.valueOf(poolsize));
            timeout = propertiesFileManager.read("shauni.properties", "database.timeout");
            if (timeout == null) {
                String thr = Thread.currentThread().getName();
                if (thr.equals("thread-1")) { //FIXME.. if the name of the thread changes, this code does not work.
                    log.warn("Global property database.timeout not found. Timeout will be reset to 5s by default.\n");
                }
                timeout = "5000";
            }
            properties.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT, timeout);

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

            dataSource = (BasicDataSource) BasicDataSourceFactory.createDataSource(properties);

        } catch (Exception e) {
            String message = "Could not create a connection pool: " + e;
            if (dataSource != null) {
                try {
                    dataSource.close();
                } catch (Exception e2) {
                    // ignore
                }
                dataSource = null;
            }
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public Connection getConnection() {
        Connection conn = null;
        try {
            if (dataSource == null) {
                log.error("Data Source is null.");
                return null;
            }
            conn = dataSource.getConnection();
            conn.setAutoCommit(false); //FIXME: hardcoded.
        } catch (SQLException e) {
            log.error("Connection pool exception\n  ->  {}", e.getMessage());
        } finally {
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
            if (dataSource != null) {
                dataSource.close();
                dataSource = null;
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
