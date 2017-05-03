package com.fil.shauni.command.support;

import java.sql.Connection;
import java.sql.Statement;

/**
 *
 * @author Shaunyl
 */
public interface StatementManager {
    Statement createStatement(Connection connection, int fetchSize);
}
