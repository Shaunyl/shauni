package com.fil.shauni.db.spring.model;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 *
 * @author Filippo
 */
@Data @Entity(name = "MontbsRunsView") @Immutable @NoArgsConstructor
public class MontbsRunView implements Serializable {

    @Id @Column(name = "run_id")
    private int runId;

    @Column(name = "host_name")
    private String hostName;

    @Column(name = "db_name")
    private String dbName;

    @Column(name = "tablespace_name")
    private String tablespaceName;

    @Column(name = "total_used_pct")
    private double totalUsedPercentage;

    @Column(name = "sample_time")
    private Timestamp sampleTime;

}

/*
Immutable: Hibernate specific...
*/
