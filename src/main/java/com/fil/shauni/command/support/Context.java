package com.fil.shauni.command.support;

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
    private final String timestamp, tableName, threadName;
}
