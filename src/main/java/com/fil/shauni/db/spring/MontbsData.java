package com.fil.shauni.db.spring;

import java.sql.Timestamp;
import java.util.Date;
import java.util.StringJoiner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Filippo
 */
@Getter @Setter @RequiredArgsConstructor
public class MontbsData {
    private final int runId, tbsRunId;
    private final MontbsDataKey key;
    private double totalUsedPercentage;
    private Date sampleTime;

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",");
        
        joiner.add(Integer.toString(this.runId));
        joiner.add(Integer.toString(this.tbsRunId));
        joiner.add(this.getKey().getHost());
        joiner.add(this.getKey().getDatabase());
        joiner.add(this.getKey().getTablespace());
        joiner.add(Double.toString(this.totalUsedPercentage));
        
        Date d = this.sampleTime;
        Timestamp ts = new Timestamp(d.getTime());
        
        joiner.add(ts.toString());
        return joiner.toString();
    }
}
