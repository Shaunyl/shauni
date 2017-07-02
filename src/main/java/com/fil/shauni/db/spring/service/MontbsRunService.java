package com.fil.shauni.db.spring.service;

import com.fil.shauni.db.spring.dao.MontbsRunRepository;
import com.fil.shauni.db.spring.model.MontbsDatabase;
import com.fil.shauni.db.spring.model.MontbsHostname;
import com.fil.shauni.db.spring.model.MontbsRun;
import com.fil.shauni.db.spring.model.MontbsTablespace;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Filippo
 */
@Service @Transactional(readOnly = true)
public class MontbsRunService implements ShauniService {

    @Autowired
    private MontbsRunRepository montbsRepository;
    
    @Autowired
    private MontbsHostnameService montbsHostnameService;

    @Autowired
    private MontbsDatabaseService montbsDatabaseService;

    @Autowired
    private MontbsTablespaceService montbsTablespaceService;

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
        
        MontbsRun run = new MontbsRun();
        
        MontbsDatabase db = montbsDatabaseService.findByDatabaseName(database);
        MontbsHostname hostname = montbsHostnameService.findByHostname(host);
        MontbsTablespace tbs = montbsTablespaceService.findByTablespaceName(tablespace);
        
        run.setMontbsDatabase(db);
        run.setMontbsHostname(hostname);
        run.setMontbsTablespace(tbs);
        run.setTotalUsedPercentage(pct);
        run.setSampleTime(time);
        
        return montbsRepository.saveAndFlush(run);
    }
}
