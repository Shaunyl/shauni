package com.fil.shauni.db.spring.dao;

import com.fil.shauni.db.spring.model.MontbsRunView;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Filippo
 */
@Repository
public interface MontbsRunViewRepository extends JpaRepository<MontbsRunView, Integer> {
    List<MontbsRunView> findByHostName(String host);
    
    List<MontbsRunView> findByHostNameAndDbNameAndTablespaceName(String host, String db, String tbs);
}
