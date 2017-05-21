package com.fil.shauni.command.export;

import java.util.Objects;

/**
 *
 * @author Filippo
 */
@FunctionalInterface
public interface SupplierPredicate {
    boolean test();
    default SupplierPredicate xor(SupplierPredicate other) {
        Objects.requireNonNull(other);
        return () -> (test() || other.test()) && !(test() && other.test());
    }
    
    default SupplierPredicate xnor(SupplierPredicate other) {
        Objects.requireNonNull(other);
        return () -> !((test() || other.test()) && !(test() && other.test()));
    }
}