package com.fil.shauni.command.support.experimental;

import com.fil.shauni.exception.ShauniException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 * @param <T>
 */
public interface Validator<T extends Object> {
    <T> ArrayList<T> validate(List<T>[] field) throws ShauniException; 
}
