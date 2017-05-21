package com.fil.shauni.command.export;

/**
 *
 * @author Filippo
 */
public class ExporterQueryObject extends ExporterObject {
    
    public ExporterQueryObject(String value) {
        super(value);
    }
    
    @Override
    public String sql() { 
        return super.get();
    }

    @Override
    public String display() {
        String obj = super.get();
        int length = obj.toString().length();
        int maxLength = 20;
        String s = obj.toString().substring(0, Math.min(maxLength, length));
        String notFitting = "";
        if (length > maxLength) {
            notFitting = "..";
        }
        return String.format("[%s%s]", s, notFitting);
    }
}
