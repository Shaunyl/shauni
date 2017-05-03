package com.fil.shauni.util.file;

import lombok.Setter;

/**
 *
 * @author Chiara
 */
public class ExportTableFilename extends DefaultFilename implements TableFilename {
    
    @Setter
    private String table;
    
    public ExportTableFilename(String path, String name) {
        super(path, name);
    }
    
    public Filename convertTable() {
        return replaceWildcard("%t", table);
    }
    
}
