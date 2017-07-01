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
@Data @Entity @Table(name = "MontbsHostnames") @NoArgsConstructor
public class MontbsHostname implements Serializable {

    @Id @Column(name = "host_id")
    private int hostId;

    @Column(name = "host_name")
    private String hostName;
}
