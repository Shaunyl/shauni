package com.fil.shauni.command.support;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author Shaunyl
 */
@Component
public class DefaultWorkSplitter<T> implements WorkSplitter<T> {
    
    @Override @SuppressWarnings("unchecked")
    public <T> Map<Integer, T[]> splitWork(Class<T> clazz, int adjustedParallel, List<T> objects) {
        int batchSize = objects.size();
            
        int rate = (int) Math.ceil((double) batchSize / (double) adjustedParallel);

        int newBatchSize = batchSize;
        int newParallel = adjustedParallel;
        int offset = 0;

        Map<Integer, T[]> list = new HashMap<>();
        for (int w = 0; w < adjustedParallel; w++) {
            T[] o = (T[])Array.newInstance(clazz, Math.min(offset + rate, batchSize) - offset);
            list.put(w, objects.subList(offset, Math.min(offset + rate, batchSize)).toArray(o));
            if (offset + rate >= batchSize) {
                break;
            }
            newBatchSize -= rate;
            newParallel -= 1;
            offset += rate;
            rate = (int) Math.ceil((double) newBatchSize / (double) newParallel);
        }
        return list;
    }  
}
