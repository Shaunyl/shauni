//package com.fil.shauni.db.spring.deprecated;
//
//import com.fil.shauni.db.spring.TestConfig;
//import com.fil.shauni.db.spring.model.MontbsRun;
//import com.fil.shauni.db.spring.model.MontbsRunKey;
//import com.fil.shauni.db.spring.deprecated.GenericDao;
//import java.io.UnsupportedEncodingException;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import javax.sql.DataSource;
//import lombok.extern.log4j.Log4j2;
//import org.apache.derby.tools.ij;
//import org.dbunit.DatabaseUnitException;
//import org.dbunit.database.DatabaseConnection;
//import org.dbunit.database.IDatabaseConnection;
//import org.dbunit.dataset.DataSetException;
//import org.dbunit.dataset.IDataSet;
//import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
//import org.dbunit.operation.DatabaseOperation;
//import org.junit.AfterClass;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.junit.Before;
//
///**
// *
// * @author Filippo Testino (filippo.testino@gmail.com)
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TestConfig.class)
//@Log4j2
//public class TestMontbsSpring {
//
//    private final static String SQLFILE = "/test/shaunidb.sql";
//    
//    private final static String DATASET = "dataset.xml";
//    
//    @Autowired @Qualifier("montbs-dao")
//    private GenericDao<MontbsRun, MontbsRunKey> repository;
//
//    @Autowired
//    private DataSource dataSource;
//
//    private static boolean initialized = false;
//
//    private static IDatabaseConnection databaseConnection;
//        
//    private static IDataSet dataSet;
//    
//    @Before
//    public void setUp() throws SQLException, UnsupportedEncodingException, DataSetException, DatabaseUnitException {
//        if (!initialized) {
//            Connection connection = dataSource.getConnection();
//            ij.runScript(connection, TestMontbsSpring.class.getResourceAsStream(SQLFILE), "UTF-8", System.out, "UTF-8");
//            dataSet = new FlatXmlDataSetBuilder()
//                    .build(Thread.currentThread().getContextClassLoader().getResourceAsStream(DATASET));
//
//            databaseConnection = new DatabaseConnection(connection);
//            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);
//            initialized = true;
//            log.info("Database initialized!");
//        }
//    }
//
//    @Test
//    public void list() throws ParseException {
////        List<MontbsRun> data = repository.list();
////
////        Assert.assertEquals(4, data.size());
////
////        MontbsRun m = new MontbsRun(3, 2, new MontbsRunKey("filippo-pc", "TESTDB", "SYSTEM"));
////        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2017-06-23 19:58:11.224");
////        m.setSampleTime(date);
////        m.setTotalUsedPercentage(86.0d);
////
////        Assert.assertEquals(m.toString(), data.get(2).toString());
//    }
//
//    @Test
//    public void findWhen() throws ParseException {
////        List<MontbsRun> data = repository.findWhen(new MontbsRunKey("filippo-pc", "TESTDB", "SYSTEM"));
////
////        Assert.assertEquals(3, data.size());
////
////        MontbsRun m = data.get(0);
////        double pct = m.getTotalUsedPercentage();
////
////        Assert.assertEquals(80, pct, 0);
//    }
//    
//    @AfterClass
//    public static void tearDown() throws DatabaseUnitException, SQLException {
//        DatabaseOperation.CLOSE_CONNECTION(DatabaseOperation.DELETE_ALL).execute(databaseConnection, dataSet);
//        log.info("Database erased!");
//    }
//}
