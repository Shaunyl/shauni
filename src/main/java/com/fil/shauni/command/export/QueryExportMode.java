package com.fil.shauni.command.export;

/**
 *
 * @author Shaunyl
 */
public class QueryExportMode implements ExportMode {

    @Override
    public String rearrangeSQL(Object obj) {
        return obj.toString();
    }
    
    @Override
    public String getName(Object obj) {
        return "";
    }
    
    @Override
    public String getShortName(Object obj) {
        return "query";
    }

}
