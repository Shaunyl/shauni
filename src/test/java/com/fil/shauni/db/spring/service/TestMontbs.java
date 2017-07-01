package com.fil.shauni.db.spring.service;

import com.fil.shauni.db.spring.TestConfig;
import com.fil.shauni.db.spring.model.MontbsRun;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.sql.DataSource;
import lombok.extern.log4j.Log4j2;
import org.apache.derby.tools.ij;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Filippo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Log4j2
public class TestMontbs {

    private final static String SQLFILE = "/sql/shaunidb.sql", DATASET = "dataset.xml";
        
    @Autowired
    private MontbsRunService service;

    @Autowired
    private DataSource dataSource;

    private static boolean initialized = false;

    private static IDatabaseConnection databaseConnection;
        
    private static IDataSet dataSet;
    
    @Before
    public void setUp() throws SQLException, UnsupportedEncodingException, DataSetException, DatabaseUnitException {
        if (!initialized) {
            Connection connection = dataSource.getConnection();
            ij.runScript(connection, TestMontbs.class.getResourceAsStream(SQLFILE), "UTF-8", System.out, "UTF-8");
            dataSet = new FlatXmlDataSetBuilder()
                    .build(Thread.currentThread().getContextClassLoader().getResourceAsStream(DATASET));

            databaseConnection = new DatabaseConnection(connection);
            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);
            initialized = true;
            log.debug("Database initialized!");
        }
    }
    
    @Test
    public void find() throws ParseException {
        List<MontbsRun> data = service.findAll();
        Assert.assertEquals(4, data.size());
        
        String s = data.get(0).toString();
        Assert.assertEquals("1,filippo-pc,TESTDB,SYSTEM,80.0,2017-06-21 19:58:19.814", s);
        
        List<MontbsRun> criticals = service.findGreaterOrEqualThanUsage(81d);
        Assert.assertEquals(2, criticals.size());
        
        List<MontbsRun> earlier = service.findEarlierOrEqualThanDate(new SimpleDateFormat("yyyy-MM-dd").parse("2017-06-22"));
        Assert.assertEquals(2, earlier.size());
        
        MontbsRun last = service.findFirstByOrderBySampleTimeDesc();
        Assert.assertEquals("3,filippo-pc,TESTDB,SYSTEM,86.0,2017-06-23 19:58:11.224", last.toString());
        
        List<MontbsRun> records = service.findAllByHostDbTbsOrderBySampleTimeDesc
            ("filippo-pc", "TESTDB", "SYSTEM");
        
        records.forEach(f -> log.info(f.toString()));
    }
    
    @AfterClass
    public static void tearDown() throws DatabaseUnitException, SQLException {
        DatabaseOperation.CLOSE_CONNECTION(DatabaseOperation.NONE).execute(databaseConnection, dataSet);
        log.debug("Database erased!");
    }
}