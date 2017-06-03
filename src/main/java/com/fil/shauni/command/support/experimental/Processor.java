package com.fil.shauni.command.support.experimental;

import lombok.NonNull;

/**
 *
 * @author Filippo
 */
@FunctionalInterface
public interface Processor<T, R> {
    default Processor<T, R> andThen(final @NonNull Processor<T, R> after) {
        return (t, c) -> after.process(process(t, c), c);
    }
    
    T process(final @NonNull T t, final @NonNull R c);
}
