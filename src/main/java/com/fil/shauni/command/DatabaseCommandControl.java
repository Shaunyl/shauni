package com.fil.shauni.command;

import com.fil.shauni.util.GeneralUtil;
import com.fil.shauni.db.DbConnection;
import com.fil.shauni.db.pool.DatabasePoolManager;
import com.fil.shauni.mainframe.ui.CommandLinePresentation;
import com.fil.shauni.util.Sysdate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public abstract class DatabaseCommandControl extends Command {
    @Inject
    protected CommandLinePresentation cli;

    protected boolean firstThread = false;

    protected String state = "completed";

    protected JdbcTemplate jdbc;

    private String host;

    private final List<DbConnection> dbcs = new ArrayList<>();

    private DataSource dataSource;
    
    protected DatabasePoolManager databasePoolManager;
    
    @Autowired(required = true)
    public void setDatabasePoolManager(final @NonNull @Value("#{databasePoolManager}") DatabasePoolManager databasePoolManager) {
        this.databasePoolManager = databasePoolManager;
    }

    @Override
    public void run(int sid) {
        this.setConnections(configuration.getWorkset());
        this.firstThread = configuration.getTid() == 0;
        DbConnection dbc = dbcs.get(sid);
        databasePoolManager.configure(dbc.getUrl(), dbc.getUser(), dbc.getPasswd(), dbc.getHost(), dbc.getSid());
        this.dataSource = databasePoolManager.getDataSource();
        this.jdbc = new JdbcTemplate(dataSource);
        this.jdbc.setFetchSize(100);
    }

    @Override
    public void takedown() {
        try {
            this.databasePoolManager.close();
        } catch (Exception e) {
            status.error();
            log.error("Pool couldn't be closed.\n{}", e.getMessage());
        }
        log.debug("Pool has been closed at " + Sysdate.now(Sysdate.TIMEONLY));
    }

    private void setConnections(final List<String> urls) {
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