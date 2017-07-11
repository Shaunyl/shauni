package com.fil.shauni.db.spring.service;

import com.fil.shauni.db.spring.dao.MontbsRunRepository;
import com.fil.shauni.db.spring.model.MontbsDatabase;
import com.fil.shauni.db.spring.model.MontbsHostname;
import com.fil.shauni.db.spring.model.MontbsRun;
import com.fil.shauni.db.spring.model.MontbsTablespace;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Filippo
 */
@Service @Transactional(readOnly = true) @Log4j2
public class MontbsRunService implements ShauniService {

    @Autowired
    private MontbsRunRepository montbsRepository;

    @Autowired
    private MontbsHostnameService montbsHostnameService;

    @Autowired
    private MontbsDatabaseService montbsDatabaseService;

    @Autowired
    private MontbsTablespaceService montbsTablespaceService;

    public long count() {
        return montbsRepository.count();
    }
    
    public List<MontbsRun> findAll() {
        return montbsRepository.findAll();
    }

    public List<MontbsRun> findByTotalUsedPercentageGreaterThanEqual(double usage) {
        return montbsRepository.findByTotalUsedPercentageGreaterThanEqual(usage);
    }

    public List<MontbsRun> findBySampleTimeGreaterThanEqual(Date date) {
        return montbsRepository.findBySampleTimeGreaterThanEqual(date);
    }

    public MontbsRun findFirstByOrderBySampleTimeDesc() {
        return montbsRepository.findFirstByOrderBySampleTimeDesc();
    }

    @Deprecated
    public List<MontbsRun> findAllByHostDbTbsOrderBySampleTimeDesc(String host, String db, String tbs) {
        return montbsRepository.findAllByHostDbTbsOrderBySampleTimeDesc(host, db, tbs);
    }

    @Transactional
    public MontbsRun persist(String host, String database, String tablespace, double pct, Timestamp time) {

        montbsHostnameService.persistIfNotExists(new MontbsHostname(host));
        montbsDatabaseService.persistIfNotExists(new MontbsDatabase(database));
        montbsTablespaceService.persistIfNotExists(new MontbsTablespace(tablespace));

        MontbsDatabase db = montbsDatabaseService.findByDatabaseName(database);
        MontbsHostname hostname = montbsHostnameService.findByHostname(host);
        MontbsTablespace tbs = montbsTablespaceService.findByTablespaceName(tablespace);

        MontbsRun run = new MontbsRun();
        run.setMontbsDatabase(db);
        run.setMontbsHostname(hostname);
        run.setMontbsTablespace(tbs);
        run.setTotalUsedPercentage(pct);
        run.setSampleTime(time);

        return montbsRepository.saveAndFlush(run);
    }

    @Transactional
    public MontbsRun persist(MontbsRun r) {
        log.debug("DATABASE (t-{}): persist -> {}", () -> Thread.currentThread().getName(), () -> r.toString());
        createRecord(r);
        return montbsRepository.saveAndFlush(r);
    }

    @Transactional
    public void bulkPersist(Collection<MontbsRun> entities, int batchSize) { // Create an interface for this..
        int i = 0;
        for (MontbsRun t : entities) {
//            createRecord(t);
            log.debug("DATABASE (t-{}): persist -> {}", () -> Thread.currentThread().getName(), () -> t.toString());
            montbsRepository.save(t);
            i++;
            if (i % batchSize == 0) {
                montbsRepository.flush();
            }
        }
    }
    
    private void createRecord(MontbsRun r) {
        MontbsHostname h = montbsHostnameService.persistIfNotExists(r.getMontbsHostname());
        MontbsDatabase d = montbsDatabaseService.persistIfNotExists(r.getMontbsDatabase());
        MontbsTablespace t = montbsTablespaceService.persistIfNotExists(r.getMontbsTablespace());
        r.setMontbsDatabase(d);
        r.setMontbsTablespace(t);
        r.setMontbsHostname(h);
    }
}

// Duplicate code..