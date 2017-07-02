package com.fil.shauni.db.spring.service;

import com.fil.shauni.db.spring.dao.MontbsRunViewRepository;
import com.fil.shauni.db.spring.model.MontbsRunView;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Filippo
 */
@Service @Transactional(readOnly = true)
public class MontbsRunViewService implements ShauniService {
    @Autowired
    private MontbsRunViewRepository montbsRepository;
    
    public List<MontbsRunView> findAll() {
        return montbsRepository.findAll();
    }
    
    public List<MontbsRunView> findByHostName(String host) {
        return montbsRepository.findByHostName(host);
    }
    
    public List<MontbsRunView> findByHostNameAndDbNameAndTablespaceName(String host, String db, String tbs) {
        return montbsRepository.findByHostNameAndDbNameAndTablespaceName(host, db, tbs);
    }
}
