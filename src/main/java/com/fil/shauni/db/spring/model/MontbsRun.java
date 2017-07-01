package com.fil.shauni.db.spring.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.StringJoiner;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Filippo
 */
@Getter @Setter @NoArgsConstructor @Entity @Table(name = "MontbsRuns")
public class MontbsRun implements Serializable {

    @EmbeddedId
    private MontbsRunKey montbsRunKey;

    @MapsId("tbs_key_id")
    @JoinColumns({
        @JoinColumn(name = "tbs_key_id", referencedColumnName = "tbs_key_id"),
    })
    @ManyToOne
    private MontbsTablespace montbsTablespace;

    @Column(name = "total_used_pct")
    private double totalUsedPercentage;

    @Column(name = "sample_time") //@Temporal(TemporalType.DATE)
    private Date sampleTime;

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",");

        joiner.add(Long.toString(this.montbsRunKey.getTbsRunId()));
        joiner.add(Long.toString(this.montbsRunKey.getTbsKeyId()));
        joiner.add(Double.toString(this.totalUsedPercentage));

        joiner.add(new Timestamp(sampleTime.getTime()).toString());
        return joiner.toString();
    }
}
