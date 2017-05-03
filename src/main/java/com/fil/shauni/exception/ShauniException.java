package com.fil.shauni.exception;

import lombok.Getter;

/**
 *
 * @author Shaunyl
 */
public class ShauniException extends Exception {

    @Getter
    private int code = 600;
    
    public ShauniException(String message) {
        super(message);
    }

    public ShauniException(int code, String message) {
        this("SHA-" + String.format("%05d", code) + ": " + message);
        this.code = code;
    }
}
