package com.fil.shauni.db.spring.dao;

import com.fil.shauni.db.spring.model.MontbsRunView;
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
public interface MontbsRunViewRepository extends JpaRepository<MontbsRunView, Integer> {
    List<MontbsRunView> findByHostName(String host);
    
    List<MontbsRunView> findByHostNameAndDbNameAndTablespaceName(String host, String db, String tbs);
    
    @Query("SELECT m FROM MontbsRunsView m WHERE m.hostName = :host AND m.dbName = :db "
            + "AND m.tablespaceName = :tbs "
            + "ORDER BY m.sampleTime DESC")
    List<MontbsRunView> findFirstOrderBySampleTimeDesc(@Param("host") String host, @Param("db") String db, @Param("tbs") String tbs);
    
    @Query("SELECT m FROM MontbsRunsView m " +
            "WHERE m.sampleTime in (SELECT MAX(n.sampleTime) FROM MontbsRunsView n GROUP BY n.tablespaceName) "
            + " AND m.hostName = :host AND m.dbName = :db")
    List<MontbsRunView> findLastRun(@Param("host")  String hostname, @Param("db") String dbname);
}