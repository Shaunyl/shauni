package com.fil.shauni.db.spring.dao;

import com.fil.shauni.db.spring.model.MontbsRun;
import java.util.Date;
import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Filippo
 */
@Repository @Scope("prototype")
public interface MontbsRunRepository extends JpaRepository<MontbsRun, Integer> {
//    @Query("SELECT r FROM MontbsRun r WHERE r.sampleTime >= :date")
//    List<MontbsRun> findEarlierOrEqualThanDate(@Param("date") Date date);
    
    List<MontbsRun> findBySampleTimeGreaterThanEqual(Date date);
    
//    @Query("SELECT r FROM MontbsRun r WHERE r.totalUsedPercentage >= :usage")
//    List<MontbsRun> findGreaterOrEqualThanUsage(@Param("usage") Double usage);
    
    List<MontbsRun> findByTotalUsedPercentageGreaterThanEqual(double pct);
    
    MontbsRun findFirstByOrderBySampleTimeDesc();
        
    @Query("SELECT r FROM MontbsRun r, MontbsHostname h, MontbsDatabase d, MontbsTablespace t"
            + " WHERE r.montbsHostname.hostId = h.hostId AND r.montbsDatabase.dbId = d.dbId"
            + "  AND r.montbsTablespace.tablespaceId = t.tablespaceId"
            + "  AND r.montbsHostname.hostName = :host AND r.montbsDatabase.dbName = :db"
            + "  AND r.montbsTablespace.tablespaceName = :tbs"
            + " ORDER BY r.sampleTime DESC") @Deprecated
    List<MontbsRun> findAllByHostDbTbsOrderBySampleTimeDesc(@Param("host") String host
            , @Param("db") String db, @Param("tbs") String tbs);
}