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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import javax.crypto.SecretKey;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;

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

    @Override
    public Long call() throws Exception {
        currentThreadName = Thread.currentThread().getName();

        String currentDate = GeneralUtil.getCurrentDate(DateFormat.TIMEONLY.toString());
        log.info("Task {} started at {}\n", currentThreadName, currentDate);
        long et = this.execute();
        currentDate = GeneralUtil.getCurrentDate(DateFormat.TIMEONLY.toString());
        log.info("\nTask {} {} at {} with {} warning(s)\nElapsed time: {} s", currentThreadName, state, currentDate, errorCount, et / 1e3);
        return et;
    }

    @Override
    public void setup() throws ShauniException {
//        this.firstThread = "thread-1".equals(currentThreadName);
//        commandLinePresentation.printIf(firstThread, LogLevel.INFO, "== Setting up");
    }

//    private void loadConnection() throws ShauniException {
//        List<String> urls = new ArrayList<>();
//        //FIXME:
//        String multidb = "-multi=y";
//        if ("-multi=y".equals(multidb)) {
//            // Decrypt..
//            this.skey = sk.getKey();
//            // check if file is already there
//            File f = new File(DatabaseConfiguration.MULTIDB_CONN_ENCRYPTED);
//            boolean isMultiDb = f.exists();
//            if (isMultiDb) {
//                try {
//                    CipherInputStream cis = sk.decrypt(f, skey);
//                    BufferedReader bread = new BufferedReader(new InputStreamReader(cis, "UTF-8"));
//
//                    String line;
//                    while ((line = bread.readLine()) != null) {
//                        String[] e = line.split("=");
//                        String ey = e[1];
//                        urls.add(ey);
//                    }
//                    bread.close();
//                    cis.close();
//                } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
//                    errorCount += 1;
//                    throw new ShauniException(1007, e.getMessage());
//                }
//            } else {
//                throw new ShauniException(1014, "No connections available.\nTip: use addcs command to add a new connection string");
//            }
////            urls.addAll(this.fileManager.readAll(MULTIDB_CONN));
//        } else {
//            urls.add(this.fileManager.read(SINGLEDB_CONN, PROP_KEY));
//        }
//        for (String u : urls) {
//            Map<String, String> map = GeneralUtil.parseConnectionString(u);
//            String user = map.get("user").toUpperCase();
//            String password = map.get("password");
//
//            String sid = map.get("sid").toUpperCase();
//            String host = map.get("host").toUpperCase();
//            DbConnection dbc = new DbConnection(u, user, password, sid, host);
//            dbcs.add(dbc);
//
////            this.databasePoolManager.configure("", user, password, 2);
//        }
//    }
    protected String state = "completed";

    @Override
    public long execute() {
        this.firstThread = "thread-1".equals(currentThreadName);
        long endTime = 0;
        try {
            commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "setup() -> start");
            this.validate();
            this.setup();
            commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "setup() -> end\n");

            for (int i = 0; i < dbcs.size(); i++) {
                databasePoolManager = new JDBCPoolManager();
                DbConnection _dbc = dbcs.get(i);
                databasePoolManager.configure(_dbc.getUrl(), _dbc.getUser(), _dbc.getPasswd(), _dbc.getHost(), _dbc.getSid());

                commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "run() -> start");
                long startTime = System.currentTimeMillis();
                this.run();
                endTime = System.currentTimeMillis() - startTime;

                this.takedown();
                commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "run() -> end");
            }
        } catch (ShauniException sh) {
            log.error(sh.getMessage());
            state = "aborted";
        }
        return endTime;
    }

    public long execute(JDBCPoolManager pool) {
        // Mock pool
        // Already configured...
        long endTime = 0;
        try {
            this.validate();
            this.setup();
            databasePoolManager = pool;

            long startTime = System.currentTimeMillis();
            this.run();
            endTime = System.currentTimeMillis() - startTime;
            this.takedown();
        } catch (ShauniException sh) {
            log.error(sh.getMessage());
            state = "aborted";
        }
        return endTime;
    }

    @Override
    public void takedown() {
        this.databasePoolManager.close();
        log.debug("Pool has been closed at " + GeneralUtil.getCurrentDate(DateFormat.TIMEONLY.toString()));
    }

    public void setConnections(final String[] urls) {
        for (String u : urls) {
            Map<String, String> map = GeneralUtil.parseConnectionString(u);
            String user = map.get("user").toUpperCase();
            String password = map.get("password");

            String sid = map.get("sid").toUpperCase();
            String host = map.get("host").toUpperCase();
            DbConnection dbc = new DbConnection(u, user, password, sid, host);
            dbcs.add(dbc);
        }
    }
}
