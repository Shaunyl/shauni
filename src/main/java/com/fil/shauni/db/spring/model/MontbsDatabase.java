package com.fil.shauni.db.spring.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
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
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "databases_seq")
    @SequenceGenerator(
            name = "databases_seq",
            sequenceName = "databases_seq",
            allocationSize = 20
    )
    private int dbId;

    @Column(name = "db_name")
    private String dbName;

    public MontbsDatabase(String dbname) {
        this.dbName = dbname;
    }
}
