package com.fil.shauni.db.spring.service;

import com.fil.shauni.db.spring.dao.MontbsTablespaceRepository;
import com.fil.shauni.db.spring.model.MontbsTablespace;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Filippo
 */
@Transactional(readOnly = true) @Service
public class MontbsTablespaceService implements ShauniService {
    @Autowired
    private MontbsTablespaceRepository montbsRepository;
    
    public MontbsTablespace findByTablespaceName(String tbsname) {
        return montbsRepository.findByTablespaceName(tbsname);
    }
    
    @Transactional
    public void persist(MontbsTablespace tbs) {
        montbsRepository.saveAndFlush(tbs);
    }
    
    public List<MontbsTablespace> findAll() {
        return montbsRepository.findAll();
    }
    
    @Transactional
    public MontbsTablespace persistIfNotExists(MontbsTablespace tbs) {
        MontbsTablespace row = findByTablespaceName(tbs.getTablespaceName());
        if (row == null) {
            return montbsRepository.saveAndFlush(tbs);
        }
        return row;
    }
}