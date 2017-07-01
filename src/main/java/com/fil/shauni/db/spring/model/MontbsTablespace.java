package com.fil.shauni.db.spring.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Filippo
 */
@Data @Entity @Table(name = "MontbsTablespaces")
public class MontbsTablespace implements Serializable {
    @Id @Column(name = "tbs_key_id")
    private long tbsRunId;
    
    @Column(name = "host_name")
    private String hostName;
    
    @Column(name = "db_name")
    private String dbName;
    
    @Column(name = "tablespace_name")
    private String tablespaceName;
}
