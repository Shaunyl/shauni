package com.fil.shauni.db.spring.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.StringJoiner;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
    @Id @Column(name = "run_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int runId;
    
    @JoinColumn(name = "host_id") @OneToOne(cascade = CascadeType.ALL)
    private MontbsHostname montbsHostname;

    @JoinColumn(name = "db_id") @OneToOne(cascade = CascadeType.ALL)
    private MontbsDatabase montbsDatabase;

    @JoinColumn(name = "tablespace_id") @OneToOne(cascade = CascadeType.ALL)
    private MontbsTablespace montbsTablespace;

    @Column(name = "total_used_pct")
    private double totalUsedPercentage;

    @Column(name = "sample_time")
    private Timestamp sampleTime;
    
    public MontbsRun(String hostname, String sid, String tbs, double pct, Timestamp time) {
        montbsHostname = new MontbsHostname(hostname);
        montbsDatabase = new MontbsDatabase(sid);
        montbsTablespace = new MontbsTablespace(tbs);
        this.totalUsedPercentage = pct;
        this.sampleTime = time;
    }

    public MontbsRun(int runId, double totalUsedPercentage, Timestamp sampleTime) {
        this.runId = runId;
        this.totalUsedPercentage = totalUsedPercentage;
        this.sampleTime = sampleTime;
    }
    
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",", "[", "]");

        joiner.add(Integer.toString(this.runId));
        joiner.add(this.montbsHostname.getHostName());
        joiner.add(this.montbsDatabase.getDbName());
        joiner.add(this.montbsTablespace.getTablespaceName());
        joiner.add(Double.toString(this.totalUsedPercentage));
        joiner.add(sampleTime.toString());

        return joiner.toString();
    }

    public MontbsRun(MontbsHostname montbsHostname, MontbsDatabase montbsDatabase, MontbsTablespace montbsTablespace, double totalUsedPercentage, Timestamp sampleTime) {
        this.montbsHostname = montbsHostname;
        this.montbsDatabase = montbsDatabase;
        this.montbsTablespace = montbsTablespace;
        this.totalUsedPercentage = totalUsedPercentage;
        this.sampleTime = sampleTime;
    }
    
    public MontbsRun(double totalUsedPercentage, Timestamp sampleTime) {
        this.totalUsedPercentage = totalUsedPercentage;
        this.sampleTime = sampleTime;
    }
}
