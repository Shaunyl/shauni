package com.fil.shauni.command.support;

import com.fil.shauni.command.export.ExporterObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Chiara
 */
@RequiredArgsConstructor
public class Context {
    @Getter
    private final int workerId, objectId;
    
    @Getter
    private final String timestamp;
    
    @Getter
    private final ExporterObject query;
    
    @Getter
    private final String threadName;
}
