package com.fil.shauni.util;

import com.fil.shauni.command.support.SupplierPredicate;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

/**
 *
 * @author Filippo
 */
public class BooleanUtils {

    public static <T> T xor(T l1, T l2) {
        boolean xor = l1 == null ^ l2 == null;
        // TRUE -> ONLY ONE IS INITIALIZED.
        if (xor) {
            return l1 == null ? l2 : l1;
        }
        // FALSE -> BOTH NULL OR INITIALIZED
        return null;
    }

    public static <E> List<E> xorOnLists(List<E> l1, List<E> l2) {
        boolean xor = l1 == null ^ l2 == null;
        if (xor) {
            return l1 == null ? l2 : l1;
        }
        if (l1 != null) {
            l1.clear();
            return l1;
        }
        return null;
    }

    public static boolean xnor(SupplierPredicate p1, SupplierPredicate p2) {
        return p1.xnor(p2).test();
    }

    public static <T, R> boolean validate(T t, R r, BiPredicate<T, R> p) {
        return p.test(t, r);
    }

    public static <T> Optional<T> getState(T t1, T t2) {
        T xor = xor(t1, t2);
        if (xor == null) {
            if (t1 == null) {
                return Optional.ofNullable(null);
            } else {
                return Optional.empty();
            }
        }
        if (t1 != null) {
            return Optional.of(t1);
        } else {
            return Optional.of(t2);
        }
    }
}
