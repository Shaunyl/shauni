package com.fil.shauni.db.spring.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.StringJoiner;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Filippo
 */
@Getter @Setter @NoArgsConstructor @Entity @Table(name = "MontbsRuns")
public class MontbsRun implements Serializable {

//    @EmbeddedId
//    private MontbsRunKey montbsRunKey;
    @Id @Column(name = "run_id")
    private int runId;

    @MapsId("host_id")
    @JoinColumns({
        @JoinColumn(name = "host_id", referencedColumnName = "host_id"),
    })
    @OneToOne
    private MontbsHostname montbsHostname;
    
    @MapsId("db_id")
    @JoinColumns({
        @JoinColumn(name = "db_id", referencedColumnName = "db_id"),
    })
    @OneToOne
    private MontbsDatabase montbsDatabase;
    
    @MapsId("tablespace_id")
    @JoinColumns({
        @JoinColumn(name = "tablespace_id", referencedColumnName = "tablespace_id"),
    })
    @OneToOne
    private MontbsTablespace montbsTablespace;
    
//    @MapsId("tbs_key_id")
//    @JoinColumns({
//        @JoinColumn(name = "tbs_key_id", referencedColumnName = "tbs_key_id"),
//    })
//    @ManyToOne
//    private MontbsTablespace montbsTablespace;

    @Column(name = "total_used_pct")
    private double totalUsedPercentage;

    @Column(name = "sample_time")
    private Timestamp sampleTime;

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",");

        joiner.add(Integer.toString(this.runId));
        joiner.add(this.montbsHostname.getHostName());
        joiner.add(this.montbsDatabase.getDbName());
        joiner.add(this.montbsTablespace.getTablespaceName());
        joiner.add(Double.toString(this.totalUsedPercentage));
        joiner.add(sampleTime.toString());
        
        return joiner.toString();
    }
}
