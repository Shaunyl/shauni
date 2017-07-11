package com.fil.shauni.db.spring.service;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.fil.shauni.db.spring.DerbyDatabase;
import com.fil.shauni.db.spring.TestConfig;
import com.fil.shauni.db.spring.model.MontbsRun;
import com.fil.shauni.util.Sysdate;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MontbsRunServiceTest {// extends AbstractTestMontbsRepository {

    protected final String format = "yyyy-MM-dd HH:mm:ss..SS";

    @Rule
    public TestRule benchmark = new BenchmarkRule();

    @Autowired
    private MontbsRunService service;

    @Rule @Inject
    public DerbyDatabase derby;

    @Test @BenchmarkOptions(benchmarkRounds = 10, warmupRounds = 1) @Ignore
    public void persist() throws ParseException {
                
//        Assert.assertEquals(0, service.findAll().size());
        
        final int n = 1000;
        List<MontbsRun> entities = createEntities(n);

        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            service.persist(entities.get(i));
        }
        long end = System.currentTimeMillis() - start;
        log.info("Persist done in {} ms", end / 1e3);

//        Assert.assertEquals(n, service.count());
    }

    @Test @BenchmarkOptions(benchmarkRounds = 10, warmupRounds = 1) //@Ignore
    public void bulkPersist() throws ParseException {
        final int n = 1000;
        List<MontbsRun> entities = createEntities(n);

        long start = System.currentTimeMillis();
        service.bulkPersist(entities, 201);
        long end = System.currentTimeMillis() - start;
        log.info("BulkPersist done in {} ms", end / 1e3);

//        Assert.assertEquals(n, service.count());
    }

    private List<MontbsRun> createEntities(int n) throws ParseException {
        List<MontbsRun> entities = new ArrayList<>(n);

        java.util.Date sampleTime = new SimpleDateFormat(format).parse(Sysdate.now(format));
        for (int i = 1; i <= n; i++) {
            entities.add(new MontbsRun("localhost", "TESTDB", "TEMP", 65.40, new Timestamp(sampleTime.getTime())));
        }
        return entities;
    }
}
