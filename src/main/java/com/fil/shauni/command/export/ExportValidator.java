package com.fil.shauni.command.export;

import com.fil.shauni.command.support.Validator;
import com.fil.shauni.exception.ShauniException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author Shaunyl
 * @param <T>
 * 
 * add filename parsing... for example: %u not supported...
 */
@Component
public class ExportValidator<T> implements Validator<T> {

    @Override
    public ArrayList<T> validate(List<T>... field) throws ShauniException {
        if (allNull(field)) {
            throw new ShauniException(1002, "At least one parameter bewteen -queries and -tables must be specified");
        }
        if (atLeastTwoNotNull(field)) {
            throw new ShauniException(1003, "Parameters -queries and -tables are mutually exclusive");
        }

        return getNotNull(field);
    }

    private Boolean allNull(List<T>... args) {
        for (List<T> arg : args) {
            if (arg != null) {
                return false;
            }
        }
        return true;
    }

    private Boolean atLeastTwoNotNull(List<T>... args) {
        int counter = 0;
        for (List<T> arg : args) {
            if (arg != null) {
                counter++;
                if (counter == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private ArrayList<T> getNotNull(List<T>... args) {
        for (List<T> arg : args) {
            if (arg != null) {
                return new ArrayList<>(arg);
            }
        }
        return null;
    }
}
