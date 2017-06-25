package com.fil.shauni.db.spring;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.apache.derby.tools.ij;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Before;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TestMontbsSpring {

    @Autowired @Qualifier("montbs-dao")
    private GenericDao<MontbsData, MontbsDataKey> repository;

    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() throws SQLException, UnsupportedEncodingException, DataSetException, DatabaseUnitException {
        Connection c = dataSource.getConnection();
        ij.runScript(c, TestMontbsSpring.class.getResourceAsStream("/test/shaunidb.sql"), "UTF-8",
                System.out, "UTF-8");
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(Thread
                .currentThread().getContextClassLoader()
                .getResourceAsStream("dataset.xml"));

        IDatabaseConnection databaseConnection = new DatabaseConnection(c);
        DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);
    }

    @Test
    public void list() throws ParseException {
        List<MontbsData> data = repository.list();
        
        Assert.assertEquals(4, data.size());

        MontbsData m = new MontbsData(3, 2, new MontbsDataKey("filippo-pc", "TESTDB", "SYSTEM"));
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2017-06-23 19:58:11.224");
        m.setSampleTime(date);
        m.setTotalUsedPercentage(86.0d);

        Assert.assertEquals(m.toString(), data.get(2).toString());
    }
}
