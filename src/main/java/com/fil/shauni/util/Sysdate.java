package com.fil.shauni.util;

import java.time.LocalDateTime;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
public class Sysdate {
    public final static String MINIMAL = "ddMMyy-HHmm";
    public final static String TIMESTAMP2 = "ddMMyyyy-HHmmss.SS";
    public final static String TIMEONLY = "HH:mm:ss.SS";
    public final static String DASH_TIMEDATE = "dd-MMM-y HH:mm:ss";
    public final static String SQUELCHED_TIMEDATE = "ddMMyyyy-HHmmss";
    
    public static String now(String format) {
        return GeneralUtil.date(format, f -> LocalDateTime.now().format(f));
    }
}
