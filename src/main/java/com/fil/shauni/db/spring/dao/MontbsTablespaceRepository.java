package com.fil.shauni.db.spring.dao;

import com.fil.shauni.db.spring.model.MontbsTablespace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Filippo
 */
@Repository
public interface MontbsTablespaceRepository extends JpaRepository<MontbsTablespace, Integer> {
    MontbsTablespace findByTablespaceName(String tbsname);
}
