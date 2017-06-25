package com.fil.shauni.db.spring;

import org.junit.Before;

/**
 *
 * @author Filippo
 */
public class TestMontbs {

    @Before
    public void SetUp() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
