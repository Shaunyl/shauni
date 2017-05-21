package com.fil.shauni.command.export;

import lombok.RequiredArgsConstructor;

/**
 *
 * @author Filippo
 */
@RequiredArgsConstructor
public abstract class ExporterObject {
    
    private final String value;
    
    public String get() {
        return value;
    }
    
    public abstract String sql();
    
    public abstract String display();
}
