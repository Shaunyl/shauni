package com.fil.shauni.command.support.worksplitter;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
public class DefaultWorkSplitter<T> implements WorkSplitter<T> {
    
    @Override
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
    
    public <T> Map<Integer, List<T>> splitWork(int parallelism, List<T> objects) {
        int batchSize = objects.size();
        
        int rate = (int) Math.ceil((double) batchSize / (double) parallelism);
        
        int newBatchSize = batchSize;
        int newParallel = parallelism;
        int offset = 0;
        
        Map<Integer, List<T>> list = new HashMap<>();
        for (int w = 0; w < parallelism; w++) {
            list.put(w, objects.subList(offset, Math.min(offset + rate, batchSize)));
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
