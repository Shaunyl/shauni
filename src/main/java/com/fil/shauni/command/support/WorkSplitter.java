package com.fil.shauni.command.support;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Shaunyl
 */
public interface WorkSplitter<T> {
    <T> Map<Integer, T[]> splitWork(Class<T> clazz, int adjustedParallel, List<T> objects);
}
