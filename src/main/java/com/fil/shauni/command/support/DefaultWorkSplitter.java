package com.fil.shauni.command.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author Shaunyl
 */
@Component
public class DefaultWorkSplitter implements WorkSplitter {
    
    @Override
    public Map<Integer, String[]> splitWork(int adjustedParallel, List<String> objects) {
        int batchSize = objects.size();
            
        int rate = (int) Math.ceil((double) batchSize / (double) adjustedParallel);

        int newBatchSize = batchSize;
        int newParallel = adjustedParallel;
        int offset = 0;

        Map<Integer, String[]> list = new HashMap<>();
        for (int w = 0; w < adjustedParallel; w++) {
            list.put(w, objects.subList(offset, Math.min(offset + rate, batchSize)).toArray(new String[Math.min(offset + rate, batchSize) - offset]));
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
