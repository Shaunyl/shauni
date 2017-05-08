package com.fil.shauni.command;

import lombok.Getter;

/**
 *
 * @author Chiara
 */
public class Check {
    @Getter
    boolean valid;
    @Getter
    String message;
    @Getter
    int code;
    
    public Check(boolean valid, int code, String message) {
        this.valid = valid;
        this.message = message;
        this.code = code;
    }
    
    public Check(int code, String message) {
        this(false, code, message);
    }
    
    public Check() {
        this(true, 0, "");
    }
}
