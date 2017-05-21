package com.fil.shauni.command.export;

/**
 *
 * @author Filippo
 */
public class ExporterTableObject extends ExporterObject {

    public ExporterTableObject(String value) {
        super(value);
    }
    
    @Override
    public String sql() {
        return String.format("SELECT * FROM %s", super.get());
    }

    @Override
    public String display() {
        return super.get();
    }

}
