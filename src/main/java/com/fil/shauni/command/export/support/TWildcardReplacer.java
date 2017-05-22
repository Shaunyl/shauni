package com.fil.shauni.command.export.support;

import com.fil.shauni.command.support.Context;
import lombok.NoArgsConstructor;
import com.fil.shauni.util.file.Filepath;

/**
 *
 * @author Chiara
 */
@NoArgsConstructor
public class TWildcardReplacer implements WildcardReplacer {

    @Override
    public void replace(Filepath in, Context context) {
        in.replaceWildcard("%t", context.getQuery().get());
    }

}