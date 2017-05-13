package com.fil.shauni.exception;

import lombok.Getter;
import org.apache.commons.lang3.text.WordUtils;

/**
 *
 * @author Shaunyl
 */
public class ShauniException extends Exception {

    @Getter
    private int code = 600;
    
    String indent = "";
    
    public ShauniException(String message) {
        super(message);
    }

    public ShauniException(int code, String message) {
        this("  SHA-" + String.format("%05d", code) + ": " + message);
        this.code = code;
        this.indent = "  ";
    }
    
    public ShauniException(int code, String message, String indent) {
        this(indent + "SHA-" + String.format("%05d", code) + ": " + message);
        this.code = code;
        this.indent = indent;
    }

    @Override
    public String getMessage() {
        return WordUtils.wrap(super.getMessage(), 140, "\n   ", false);
    }
    
    
}
