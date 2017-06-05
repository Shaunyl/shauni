package com.fil.shauni.exception;

/**
 *
 * @author Filippo Testino
 */
public class ValidationException extends RuntimeException {

    public ValidationException(Throwable t) {
        super(t);
    }

    public ValidationException(String string) {
        super(string);
    }

    public ValidationException(String string, Throwable t) {
        super(string, t);
    }
}
