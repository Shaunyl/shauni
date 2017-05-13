package com.fil.shauni.command;

//import com.fil.mvc.BeanFactory;
import com.fil.shauni.command.crypto.StoreKey;
import com.fil.shauni.util.GeneralUtil;
import com.fil.shauni.db.DbConnection;
import com.fil.shauni.db.pool.DatabasePoolManager;
import com.fil.shauni.db.pool.JDBCPoolManager;
import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.log.LogLevel;
import com.fil.shauni.mainframe.ui.CommandLinePresentation;
import com.fil.shauni.util.DateFormat;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import javax.crypto.SecretKey;
import javax.inject.Inject;
import javax.sql.DataSource;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Shaunyl
 */
@Log4j2
public abstract class DatabaseCommandControl extends Command {

    // temp ignorable for now..
    private static final String PROP_KEY = "url";

    // encrypt these file to raise security..
    private final static String SINGLEDB_CONN = DatabaseConfiguration.SINGLEDB_CONN;

    private final static String MULTIDB_CONN = DatabaseConfiguration.MULTIDB_CONN;

    protected DatabasePoolManager databasePoolManager;

    @Inject
    private StoreKey sk;

    private SecretKey skey;

    protected ExecutorService executorService;

    private final List<DbConnection> dbcs = new ArrayList<>();

    protected String currentThreadName = "";

    @Inject
    protected CommandLinePresentation commandLinePresentation;

    protected boolean firstThread = false;

    protected String state = "completed";

    protected JdbcTemplate jdbc;
    
    @Override
    public Long call() throws Exception {
        currentThreadName = Thread.currentThread().getName();

        String currentDate = GeneralUtil.getCurrentDate(DateFormat.TIMEONLY.toString());
        log.info("Session {} started at {}\n", currentThreadName, currentDate);
        long et = this.execute();
        currentDate = GeneralUtil.getCurrentDate(DateFormat.TIMEONLY.toString());
        log.info("\nSession {} {} at {} with {} warning(s)\nElapsed time: {} s", currentThreadName, state, currentDate, errorCount, et / 1e3);
        return et;
    }

    @Override
    public void setup() throws ShauniException {
//        commandLinePresentation.printIf(firstThread, LogLevel.INFO, "== Setting up");
    }

    @Override
    public long execute() {
        this.firstThread = "thread-1".equals(currentThreadName);
        long endTime = 0;
        try {
//            commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "setup() -> start");
            commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "Validating..");
            Check check = this.validate();
            if (!check.isValid()) {
                throw new ShauniException(check.getCode(), check.getMessage());
            }

            commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "\nSetting up..");
            this.setup();
//            commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "setup() -> end\n");
            databasePoolManager = new JDBCPoolManager(); // Untestable...try to inject in the constructor...
            for (int i = 0; i < dbcs.size(); i++) {
                DbConnection _dbc = dbcs.get(i);
                databasePoolManager.configure(_dbc.getUrl(), _dbc.getUser(), _dbc.getPasswd(), _dbc.getHost(), _dbc.getSid());
                this.setDataSource(databasePoolManager.getDataSource());

//                commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "run() -> start");
                long startTime = System.currentTimeMillis();
                this.run();
                long finishTime = System.currentTimeMillis() - startTime;;
                endTime += finishTime;
                log.info("Session {} task #{} finished in {} ms", currentThreadName, i, finishTime / 1e3);

                this.takedown();
//                commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "run() -> end");
            }
        } catch (ShauniException sh) {
            log.error(sh.getMessage());
            state = "aborted";
        }
        return endTime;
    }

    DataSource ds;
    public void setDataSource(DataSource ds) {
        this.ds = ds;
        this.jdbc = new JdbcTemplate(ds);
        this.jdbc.setFetchSize(100); // FIXME: not here.. (should override PreparedStatementCreator and pass it to jdbcTemplate
    }

    public Connection getConnection(int workerId) throws ShauniException {
        Connection connection = databasePoolManager.getConnection(); // this need to be here because every thread needs to create a different connection.
        if (connection == null) {
            log.error("> Worker {} could not connect to {}@{}", workerId, databasePoolManager.getSid(), databasePoolManager.getHost());
//            throw new ShauniException("> Worker " + workerId + " could not connect to " + databasePoolManager.getSid() + "@" + databasePoolManager.getHost());
        }
        return connection;
    }

    @Override
    public void takedown() {
        this.databasePoolManager.close();
        log.debug("Pool has been closed at " + GeneralUtil.getCurrentDate(DateFormat.TIMEONLY.toString()));
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
            String host = map.get("host").toUpperCase();
            DbConnection dbc = new DbConnection(u, user, password, sid, host);
            dbcs.add(dbc);
        }
    }
}
