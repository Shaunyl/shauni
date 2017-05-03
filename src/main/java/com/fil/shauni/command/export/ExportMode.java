package com.fil.shauni.command.export;

/**
 *
 * @author Shaunyl
 */
public interface ExportMode {
    String rearrangeSQL(Object obj);
    
    String getName(Object obj);
    
    String getShortName(Object obj);
}
