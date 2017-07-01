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
public class MontbsRunService {
    @Autowired
    private MontbsRunRepository montbsRepository;
    
    public List<MontbsRun> findAll() {
        return montbsRepository.findAll();
    }
    
    public List<MontbsRun> findByUsage(double usage) {
        return montbsRepository.findByUsage(usage);
    }
    
    public List<MontbsRun> findByDate(Date date) {
        return montbsRepository.findByDate(date);
    }
}
