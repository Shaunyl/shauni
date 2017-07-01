package com.fil.shauni.db.spring.dao;

import com.fil.shauni.db.spring.model.MontbsHostname;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Filippo
 */
@Repository
public interface MontbsHostnameRepository extends JpaRepository<MontbsHostname, Integer> {
}
