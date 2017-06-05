package com.fil.shauni.exception;

import lombok.Getter;
import org.apache.commons.lang3.text.WordUtils;

/**
 *
 * @author Shaunyl
 */
public class ShauniException extends RuntimeException {

    @Getter
    private int code = 600;
    
    public ShauniException(String message) {
        super(message);
    }

    public ShauniException(int code, String message) {
        this("  SHA-" + String.format("%05d", code) + ": " + message);
        this.code = code;
    }

    @Override
    public String getMessage() {
        return WordUtils.wrap(super.getMessage(), 140, "\n   ", false);
    } 
}
