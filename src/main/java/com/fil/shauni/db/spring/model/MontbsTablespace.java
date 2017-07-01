package com.fil.shauni.db.spring.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Filippo
 */
@Data @Entity @Table(name = "MontbsTablespaces") @NoArgsConstructor
public class MontbsTablespace implements Serializable {
    @Id @Column(name = "tablespace_id")
    private int tablespaceId;
    
    @Column(name = "tablespace_name")
    private String tablespaceName;
}
