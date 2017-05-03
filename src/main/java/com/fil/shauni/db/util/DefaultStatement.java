package com.fil.shauni.db.util;

import com.fil.shauni.command.support.StatementManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author Shaunyl
 */
@Log4j @Component
public class DefaultStatement implements StatementManager {

    @Override
    public Statement createStatement(Connection connection, int fetchSize) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.setFetchSize(fetchSize);
        } catch (SQLException e) {
            log.error("Error while creating the statement\n  -> " + e.getMessage());
            throw new RuntimeException();
        }
        return statement;
    }

}
