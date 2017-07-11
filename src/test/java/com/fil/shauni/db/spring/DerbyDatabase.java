package com.fil.shauni.db.spring;

import com.fil.shauni.db.spring.service.TestMontbsRepositories;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.derby.tools.ij;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.rules.ExternalResource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Filippo
 */
@Log4j2 @NoArgsConstructor
public final class DerbyDatabase extends ExternalResource {

    private IDatabaseConnection databaseConnection;

    private IDataSet dataSet;

    private final static String SQLFILE = "sql/shaunidb.sql", DATASET = "test/dataset.xml";

//    private static boolean initialized = false;
        
    @Autowired
    private DataSource dataSource;

    @Override
    protected void before() throws Throwable {
        log.info("BEFORE");
//        if (!initialized) {
//            Connection connection = dataSource.getConnection();
//            ij.runScript(connection, getClass().getClassLoader().getResourceAsStream(SQLFILE), "UTF-8", System.out, "UTF-8");
//            dataSet = new FlatXmlDataSetBuilder()
//                    .build(getClass().getClassLoader().getResourceAsStream(DATASET));
//
//            databaseConnection = new DatabaseConnection(connection);
//            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);
//            initialized = true;
//            log.debug("Database initialized!");
//        }
    }

    @Override
    protected void after() {
        log.info("AFTER");
//        try {
//            DatabaseOperation.CLOSE_CONNECTION(DatabaseOperation.NONE).execute(databaseConnection, dataSet);
//        } catch (DatabaseUnitException | SQLException e) {
//            log.error("Error while clearing database " + e.getMessage());
//        }
//        log.debug("Database cleared!");
    }
}
