package com.fil.shauni.db.spring.service;

import com.fil.shauni.db.spring.dao.MontbsRunRepository;
import com.fil.shauni.db.spring.model.MontbsRun;
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
    
    public List<MontbsRun> findAll() {
        return montbsRepository.findAll();
    }
    
    public List<MontbsRun> findGreaterOrEqualThanUsage(double usage) {
        return montbsRepository.findGreaterOrEqualThanUsage(usage);
    }
    
    public List<MontbsRun> findEarlierOrEqualThanDate(Date date) {
        return montbsRepository.findEarlierOrEqualThanDate(date);
    }
    
    public MontbsRun findFirstByOrderBySampleTimeDesc() {
        return montbsRepository.findFirstByOrderBySampleTimeDesc();
    }
    
    public List<MontbsRun> findAllByHostDbTbsOrderBySampleTimeDesc(String host, String db, String tbs) {
        return montbsRepository.findAllByHostDbTbsOrderBySampleTimeDesc(host, db, tbs);
    }
}
