package com.fil.shauni.command.export.support;

import com.fil.shauni.command.support.Context;
import com.fil.shauni.util.file.Filename;

/**
 *
 * @author Chiara
 */
public interface WildcardReplacer {
    Filename replace(Filename in, Context context);
}
