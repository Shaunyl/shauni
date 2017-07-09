package com.fil.shauni.db.spring.service;

import com.fil.shauni.db.spring.dao.MontbsDatabaseRepository;
import com.fil.shauni.db.spring.model.MontbsDatabase;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Filippo
 */
@Transactional(readOnly = true) @Service
public class MontbsDatabaseService implements ShauniService {

    @Autowired
    private MontbsDatabaseRepository montbsRepository;

    public MontbsDatabase findByDatabaseName(String dbname) {
        return montbsRepository.findByDbName(dbname);
    }

    @Transactional
    public void persist(MontbsDatabase db) {
        montbsRepository.saveAndFlush(db);
    }

    public List<MontbsDatabase> findAll() {
        return montbsRepository.findAll();
    }

    @Transactional
    public MontbsDatabase persistIfNotExists(MontbsDatabase db) {
        MontbsDatabase row = findByDatabaseName(db.getDbName());
        if (row == null) {
            return montbsRepository.saveAndFlush(db);
        }
        return row;
    }
}
