package com.fil.shauni.command.export.support;

import com.fil.shauni.command.support.Context;
import com.fil.shauni.util.file.Filepath;

/**
 *
 * @author Chiara
 */
public interface WildcardReplacer {
    void replace(Filepath in, Context context);
}
