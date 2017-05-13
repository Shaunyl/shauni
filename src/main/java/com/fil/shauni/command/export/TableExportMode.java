package com.fil.shauni.command.export;

/**
 *
 * @author Shaunyl
 */
public class TableExportMode implements ExportMode {

    @Override
    public String rearrangeSQL(Object obj) {
        return String.format("SELECT * FROM %s", obj);
    }
    
    @Override
    public String getName(Object obj) {
        return obj.toString().replace("$", "\\$").toUpperCase();
    }
    
    @Override
    public String getShortName(Object obj) {
        return getName(obj);
    }
}
