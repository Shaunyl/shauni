package com.fil.shauni.command.export;

import com.fil.shauni.exception.ShauniException;

/**
 *
 * @author Shaunyl
 */
public interface Exporter {
    void export(final int worker, final Object[] set) throws ShauniException;
}
