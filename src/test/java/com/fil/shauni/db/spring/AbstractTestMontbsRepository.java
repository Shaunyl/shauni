package com.fil.shauni.db.spring;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.BeforeClass;

/**
 *
 * @author Filippo
 */
@Log4j2
public abstract class AbstractTestMontbsRepository {   
    protected final String format = "yyyy-MM-dd HH:mm:ss..SS";
    
    protected final static List<String> TABLESPACES = new ArrayList<>(4);

    @BeforeClass
    public static void setUpClass() {
        TABLESPACES.add("SYSTEM");
        TABLESPACES.add("SYSAUX");
        TABLESPACES.add("DBA_TBS");
        TABLESPACES.add("UNDOTBS1");
    }
}
