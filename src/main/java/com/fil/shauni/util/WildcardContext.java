package com.fil.shauni.util;

import com.fil.shauni.command.export.ExporterObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Filippo
 */
@RequiredArgsConstructor
public class WildcardContext {

    @Getter
    private final int workerId, objectId;

    @Getter
    private final String timestamp;

    @Getter
    private final ExporterObject query;

    @Getter
    private final String threadName;

}
