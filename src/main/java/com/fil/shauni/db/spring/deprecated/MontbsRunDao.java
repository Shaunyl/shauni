package com.fil.shauni.db.spring.deprecated;

import com.fil.shauni.db.spring.model.MontbsRun;
import com.fil.shauni.db.spring.model.MontbsRunKey;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Filippo
 */
@Repository("montbs-dao") @Log4j2
public class MontbsRunDao extends SpringGenericDao<MontbsRun, MontbsRunKey> {   
    @Override
    public void add(MontbsRun entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove(MontbsRun entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(MontbsRun entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MontbsRun find(MontbsRunKey key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<MontbsRun> findWhen(MontbsRunKey key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override @Transactional @SuppressWarnings("unchecked")
    public List<MontbsRun> list() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery criteria = cb.createQuery();
        criteria.select(criteria.from(MontbsRun.class));

        List<MontbsRun> montbs = entityManager.createQuery(criteria).getResultList();
        return montbs;
    }

}
