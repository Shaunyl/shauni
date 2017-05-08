package com.fil.shauni.mainframe.spi;

import com.fil.shauni.exception.ShauniException;

/**
 *
 * @author Chiara
 */
public interface CommandBuilder {
    public void initialize(CommandContext ctx) throws ShauniException;
}
