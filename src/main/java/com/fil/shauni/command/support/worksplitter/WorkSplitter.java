package com.fil.shauni.command.support.worksplitter;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
public interface WorkSplitter<T> {
    <T> Map<Integer, T[]> splitWork(Class<T> clazz, int adjustedParallel, List<T> objects);
    
    <T> Map<Integer, List<T>> splitWork(int parallelism, List<T> objects);
}
