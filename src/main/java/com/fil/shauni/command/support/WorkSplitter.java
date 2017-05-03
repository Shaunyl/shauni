package com.fil.shauni.command.support;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Shaunyl
 */
public interface WorkSplitter {
    Map<Integer, String[]> splitWork(int adjustedParallel, List<String> objects);
}
