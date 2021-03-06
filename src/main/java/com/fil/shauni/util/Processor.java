package com.fil.shauni.util;

import lombok.NonNull;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@FunctionalInterface
public interface Processor<T, R> {
    default Processor<T, R> andThen(final @NonNull Processor<T, R> after) {
        return (t, c) -> after.process(process(t, c), c);
    }
    
    T process(final @NonNull T t, final @NonNull R c);
}