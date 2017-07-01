package com.fil.shauni.db.spring.deprecated;

//package com.fil.shauni.db.spring;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//@Repository(value = "montbs-dao")
//public abstract class JdbcTemplateGenericDao implements GenericDao<MontbsRun, MontbsRunKey> {
//    
//    @Autowired
//    protected JdbcTemplate jdbcTemplate;
//}

/*

    @Override
    public List<MontbsRun> findWhen(MontbsRunKey key) {
//        return jdbcTemplate.query("SELECT * FROM MontbsRuns WHERE"
//                + " host_name = ?"
//                + " AND db_name = ?"
//                + " AND tablespace_name = ?", new Object[] { key.getHost(), key.getDatabase(), key.getTablespace() },
//                new MontbsDataRowMapper());
    }

    @Override
    public List<MontbsRun> list() {
        return jdbcTemplate.query("SELECT * FROM MontbsRuns", new MontbsDataRowMapper());
    }


*/