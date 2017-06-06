package com.fil.shauni.command.export.support;

import com.fil.shauni.command.support.Exportable;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * @author Filippo
 */
@NoArgsConstructor
public abstract class Entity implements Exportable {
    @Getter
    protected String obj;
    
    public Entity(String obj) {
        this.obj = obj;
    }
    
    public String convert() {
        return convert(obj);
    }
    
    public String display() {
        return display(obj);
    }
}
