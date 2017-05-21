package com.fil.shauni.util;

import com.fil.shauni.command.export.SupplierPredicate;

/**
 *
 * @author Filippo
 */
public class BooleanUtils<T> {
    public static <T> T xor(T l1, T l2) {
        boolean xor = l1 == null ^ l2 == null;
        // TRUE -> ONLY ONE IS INITIALIZED.
        if (xor) {
            return l1 == null ? l2 : l1;
        }
        // FALSE -> BOTH NULL OR INITIALIZED
        return null;
    }
    
    public static boolean xnor(SupplierPredicate p1, SupplierPredicate p2) {
        return p1.xnor(p2).test();
    }
}
