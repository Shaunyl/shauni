package com.fil.shauni.command.export.support;

import com.fil.shauni.command.support.Context;
import com.fil.shauni.util.file.Filename;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Chiara
 */
@Component @NoArgsConstructor
public class WWildcardReplacer implements WildcardReplacer {

    @Override
    public Filename replace(Filename in, Context context) {
        return in.replaceWildcard("%w", context.getWorkerId());
    }
    
}
