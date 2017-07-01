package com.fil.shauni.db.spring.dao;

import com.fil.shauni.db.spring.model.MontbsRun;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Filippo
 */
@Repository
public interface MontbsRunRepository extends JpaRepository<MontbsRun, Integer> {
    @Query("SELECT r FROM MontbsRun r WHERE r.sampleTime >= :date")
    List<MontbsRun> findByDate(@Param("date") Date date);
    
    @Query("SELECT r FROM MontbsRun r WHERE r.totalUsedPercentage >= :usage")
    List<MontbsRun> findByUsage(@Param("usage") Double usage);
}
