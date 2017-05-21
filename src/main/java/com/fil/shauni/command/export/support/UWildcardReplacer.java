package com.fil.shauni.command.export.support;

import com.fil.shauni.command.support.Context;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import com.fil.shauni.util.file.Filepath;

/**
 *
 * @author Chiara
 */
@Component @NoArgsConstructor
public class UWildcardReplacer implements WildcardReplacer {

    @Override
    public void replace(Filepath in, Context context) {
        in.replaceWildcard("%u", context.getObjectId());
    }

}
