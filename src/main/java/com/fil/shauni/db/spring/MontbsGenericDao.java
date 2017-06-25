package com.fil.shauni.db.spring;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository(value = "montbs-dao")
public class MontbsGenericDao implements GenericDao<MontbsData, MontbsDataKey> {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void add(MontbsData entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove(MontbsData entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(MontbsData entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MontbsData find(MontbsDataKey key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<MontbsData> findWhen(MontbsDataKey key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<MontbsData> list() {
        return jdbcTemplate.query("SELECT * FROM MontbsRuns", new MontbsDataRowMapper());
    }
}
