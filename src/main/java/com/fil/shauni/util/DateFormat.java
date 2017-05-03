package com.fil.shauni.util;

import lombok.RequiredArgsConstructor;

/**
 *
 * @author Shaunyl
 */
@RequiredArgsConstructor
public enum DateFormat {
    
    TIMEONLY("HH:mm:ss.SS"),
    SQUELCHED_TIMEDATE("ddMMyyyy-HHmmss"),
    DASH_TIMEDATE("dd-MMM-y HH:mm:ss"),
    CLEAN_DATETIME("ddMMyy-HHmm");
    
    private final String date;

    @Override
    public String toString() {
        return date;
    }
}