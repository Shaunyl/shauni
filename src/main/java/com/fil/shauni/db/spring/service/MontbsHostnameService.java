package com.fil.shauni.db.spring.service;

import com.fil.shauni.db.spring.dao.MontbsHostnameRepository;
import com.fil.shauni.db.spring.model.MontbsHostname;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Filippo
 */
@Transactional(readOnly = true) @Service
public class MontbsHostnameService implements ShauniService {
    @Autowired
    private MontbsHostnameRepository montbsRepository;
    
    public MontbsHostname findByHostname(String hostname) {
        return montbsRepository.findByHostName(hostname);
    }
    
    @Transactional
    public void persist(MontbsHostname host) {
        montbsRepository.saveAndFlush(host);
    }
    
    public List<MontbsHostname> findAll() {
        return montbsRepository.findAll();
    }
    
    @Transactional
    public MontbsHostname persistIfNotExists(MontbsHostname host) {
        if (findByHostname(host.getHostName()) == null) {
            return montbsRepository.saveAndFlush(host);
        }
        return null;
    }
}
