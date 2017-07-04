package com.fil.shauni.db.spring.service;

import com.fil.shauni.db.spring.TestConfig;
import com.fil.shauni.db.spring.model.MontbsHostname;
import com.fil.shauni.db.spring.model.MontbsRun;
import com.fil.shauni.db.spring.model.MontbsRunView;
import com.fil.shauni.util.SpringContext;
import com.fil.shauni.util.Sysdate;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.PersistenceException;
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
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Filippo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Log4j2 @ActiveProfiles({ "test" })
public class TestMontbsRepositories {

    private final static String SQLFILE = "/sql/shaunidb.sql", DATASET = "test/dataset.xml";

    @Autowired
    private MontbsRunService service;

    @Autowired
    private MontbsHostnameService montbsHostnameService;

    @Autowired
    private MontbsRunViewService montbsRunViewService;

    @Autowired
    private DataSource dataSource;
    
    private final String format = "yyyy-MM-dd HH:mm:ss..SS";

    private static boolean initialized = false;

    private static IDatabaseConnection databaseConnection;

    private static IDataSet dataSet;

    private final static List<String> TABLESPACES = new ArrayList<>(4);

    @BeforeClass
    public static void setUpClass() {
        TABLESPACES.add("SYSTEM");
        TABLESPACES.add("SYSAUX");
        TABLESPACES.add("DBA_TBS");
        TABLESPACES.add("UNDOTBS1");
    }

    @Before
    public void setUp() throws SQLException, UnsupportedEncodingException, DataSetException, DatabaseUnitException {
        if (!initialized) {
            Connection connection = dataSource.getConnection();
            ij.runScript(connection, TestMontbsRepositories.class.getResourceAsStream(SQLFILE), "UTF-8", System.out, "UTF-8");
            dataSet = new FlatXmlDataSetBuilder()
                    .build(Thread.currentThread().getContextClassLoader().getResourceAsStream(DATASET));

            databaseConnection = new DatabaseConnection(connection);
            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);
            initialized = true;
            log.debug("Database initialized!");
        }
    }

    @Test @Ignore
    public void find() throws ParseException {
        List<MontbsRun> data = service.findAll();
        Assert.assertEquals(11, data.size());

        String s = data.get(0).toString();
        Assert.assertEquals("1,FILIPPO-PC,TESTDB,SYSTEM,80.0,2017-06-21 19:58:19.814", s);

        List<MontbsRun> criticals = service.findByTotalUsedPercentageGreaterThanEqual(81d);
        Assert.assertEquals(3, criticals.size());

        List<MontbsRun> earlier = service.findBySampleTimeGreaterThanEqual(new SimpleDateFormat("yyyy-MM-dd").parse("2017-06-22"));
        Assert.assertEquals(2, earlier.size());

        MontbsRun last = service.findFirstByOrderBySampleTimeDesc();
        Assert.assertEquals("3,FILIPPO-PC,TESTDB,SYSTEM,86.0,2017-06-23 19:58:11.224", last.toString());
    }

    @Test @Ignore @Deprecated
    public void findAllByHostDbTbsOrderBySampleTimeDesc() {
        long start = System.currentTimeMillis();
        int s = service.findAllByHostDbTbsOrderBySampleTimeDesc("FILIPPO-PC", "XE", "SYSTEM").size();
        long end = System.currentTimeMillis() - start;
        String logs = String.format("Time: %s - Elapsed time: %d ms - Count: %d\n", Sysdate.now(Sysdate.TIMEONLY),
                end, s);

        try {
            Files.write(Paths.get("perfeva.txt"), logs.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {

        }
    }

    @Test @Repeat(value = 100) @Ignore
    public void findAllByHostDbTbsOrderBySampleTimeDescMultipleTimes() throws IOException {
        Files.write(Paths.get("perfeva.txt"), "Test on MontbsRunsView view -> ".getBytes(), StandardOpenOption.APPEND);

        long start = System.currentTimeMillis();
        int s = 0;
        for (int i = 0; i < TABLESPACES.size(); i++) {
            s += montbsRunViewService.findByHostNameAndDbNameAndTablespaceName("FILIPPO-PC", "XE", TABLESPACES.get(i)).size();
        }
        long end = System.currentTimeMillis() - start;
        String logs = String.format("Time: %s - Elapsed time: %d ms - Count: %d\n", Sysdate.now(Sysdate.TIMEONLY),
                end, s);

        Assert.assertTrue("Elapsed Time greater than 5s", end <= 50000);

        Files.write(Paths.get("perfeva.txt"), logs.getBytes(), StandardOpenOption.APPEND);
    }

    @Test @Repeat(value = 100) @Deprecated @Ignore
    public void findAllByHostAndDatabaseAndTablespace() throws IOException {
        Files.write(Paths.get("perfeva.txt"), "Test on MontbsRuns table -> ".getBytes(), StandardOpenOption.APPEND);

        long start = System.currentTimeMillis();
        int s = 0;
        for (int i = 0; i < TABLESPACES.size(); i++) {
            s += service.findAllByHostDbTbsOrderBySampleTimeDesc("FILIPPO-PC", "XE", TABLESPACES.get(i)).size();
        }
        long end = System.currentTimeMillis() - start;
        String logs = String.format("Time: %s - Elapsed time: %d ms - Count: %d\n", Sysdate.now(Sysdate.TIMEONLY),
                end, s);

        Assert.assertTrue("Elapsed Time greater than 5s", end <= 50000);

        Files.write(Paths.get("perfeva.txt"), logs.getBytes(), StandardOpenOption.APPEND);
    }

    @Test @Ignore
    public void findByHostName() {
        MontbsHostname records = montbsHostnameService.findByHostname("FILIPPO-PC");
        Assert.assertNotNull(records);

        Assert.assertEquals("FILIPPO-PC", records.getHostName());

        MontbsHostname notfound = montbsHostnameService.findByHostname("not-here");
        Assert.assertNull(notfound);
    }

    @Test(expected = PersistenceException.class) @Ignore
    public void persistHost() {
        montbsHostnameService.persist(new MontbsHostname("FILIPPO-PC"));
        int size = montbsHostnameService.findAll().size();
        Assert.assertEquals(3, size);

        MontbsHostname persisted = montbsHostnameService.persistIfNotExists(new MontbsHostname("host-test5"));
        Assert.assertNull(persisted);
    }
    
    @Test
    public void findFirstMontbsRunViewRecordOrderByDesc() {
        MontbsRunView row = montbsRunViewService
                .findFirstOrderBySampleTimeDesc("FILIPPO-PC", "XE", "SYSTEM");
        double pct = row.getTotalUsedPercentage();
        Assert.assertEquals(86.21, pct, 0);
    }

    @Test(expected = PersistenceException.class) @Ignore
    public void persistRunThrowPersistenceException() throws ParseException {
        java.util.Date sampleTime = new SimpleDateFormat(format).parse(Sysdate.now(format));
        service.persist("localhost", "TESTDB", "TEMP", 65.40, new Timestamp(sampleTime.getTime()));
    }
    
    @Test @Ignore
    public void persistRun() throws ParseException {
        java.util.Date sampleTime = new SimpleDateFormat(format).parse(Sysdate.now(format));
//        SpringContext.getApplicationContext().getBean(MontbsRunService.class).persist("localhost", "ERMDB", "TEMP", 45.12, new Timestamp(sampleTime.getTime()));
        service.persist("localhost", "ERMDB", "TEMP", 45.12, new Timestamp(sampleTime.getTime()));
    }

    @AfterClass
    public static void tearDown() throws DatabaseUnitException, SQLException {
        DatabaseOperation.CLOSE_CONNECTION(DatabaseOperation.NONE).execute(databaseConnection, dataSet);
        log.debug("Database erased!");
    }
}
