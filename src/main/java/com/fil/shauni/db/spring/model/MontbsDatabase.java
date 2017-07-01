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
@Data @Entity @Table(name = "MontbsDatabases") @NoArgsConstructor
public class MontbsDatabase implements Serializable {

    @Id @Column(name = "db_id")
    private int dbId;

    @Column(name = "db_name")
    private String dbName;
}
