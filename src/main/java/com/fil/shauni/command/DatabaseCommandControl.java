package com.fil.shauni.command;

import com.fil.shauni.command.crypto.StoreKey;
import com.fil.shauni.util.GeneralUtil;
import com.fil.shauni.db.DbConnection;
import com.fil.shauni.db.pool.DatabasePoolManager;
import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.mainframe.impl.cli.CommandLine;
import com.fil.shauni.mainframe.ui.CommandLinePresentation;
import com.fil.shauni.util.Sysdate;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.inject.Inject;
import javax.sql.DataSource;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author Shaunyl
 */
@Log4j2 @Component
public abstract class DatabaseCommandControl<T> extends Command<T> {

    // temp ignorable for now..
    private static final String PROP_KEY = "url";

    // encrypt these file to raise security..
    private final static String SINGLEDB_CONN = DatabaseConfiguration.SINGLEDB_CONN;

    private final static String MULTIDB_CONN = DatabaseConfiguration.MULTIDB_CONN;

    @Inject
    private StoreKey sk;

    private SecretKey skey;

    private final List<DbConnection> dbcs = new ArrayList<>();

//    @Inject TEMPME
    protected CommandLinePresentation cli = new CommandLine();

    protected boolean firstThread = false;

    protected String state = "completed";

    protected JdbcTemplate jdbc;

    private String host;

    // Only for testing purpose
//    public void setCurrentThreadName(String name) {
//        currentThreadName = name;
//    }
    
    private DataSource dataSource;
    
    protected DatabasePoolManager databasePoolManager;
    
    @Autowired(required = true)
    public void setDatabasePoolManager(final @NonNull @Value("#{databasePoolManager}") DatabasePoolManager databasePoolManager) {
        this.databasePoolManager = databasePoolManager;
    }

    @Override
    public Long call() throws Exception {
        _thread = Thread.currentThread().getName();

        String currentDate = Sysdate.now(Sysdate.TIMEONLY);
        log.info("Session {} started at {}\n", _thread, currentDate);
        long et = this.execute();
        currentDate = Sysdate.now(Sysdate.TIMEONLY);
        log.info("\nSession {} {} at {} with {} warning(s)\nElapsed time: {} s", _thread, state, currentDate, getErrorCount(), et / 1e3);
        return et;
    }

    @Override
    public long execute() {
        this.firstThread = "thread-1".equals(_thread);
        return super.execute();
    }

    @Override
    public void setup() throws ShauniException {
        threads = dbcs.size();
        super.setup();
    }

    @Override
    protected void initThread(int i) {
        DbConnection _dbc = dbcs.get(i);
        databasePoolManager.configure(_dbc.getUrl(), _dbc.getUser(), _dbc.getPasswd(), _dbc.getHost(), _dbc.getSid());
        this.dataSource = databasePoolManager.getDataSource();
        this.jdbc = new JdbcTemplate(dataSource);
        this.jdbc.setFetchSize(100);
    }

    public Connection getConnection(int workerId) throws ShauniException {
        Connection connection = databasePoolManager.getConnection(); // this need to be here because every thread needs to create a different connection.
        if (connection == null) {
            incrementErrorCount();
            log.error("> Worker {} could not connect to {}@{}", workerId, databasePoolManager.getSid(), databasePoolManager.getHost());
        }
        return connection;
    }

    @Override
    public void takedownThread() {
        this.databasePoolManager.close();
        log.debug("Pool has been closed at " + Sysdate.now(Sysdate.TIMEONLY));
    }

    public void setConnections(final String[] urls) {
        for (String u : urls) {
            if ("".equals(u)) {
                log.info("Connection string not set (missing the '=' sign?). It will be skipped.");
                continue;
            }
            Map<String, String> map = GeneralUtil.parseConnectionString(u);
            if (map == null) {
                log.info("Connection string is not valid. It will be skipped.");
                continue;
            }
            String user = map.get("user").toUpperCase();
            String password = map.get("password");

            String sid = map.get("sid").toUpperCase();
            host = map.get("host").toUpperCase();
            DbConnection dbc = new DbConnection(u, user, password, sid, host);
            dbcs.add(dbc);
        }
    }
}