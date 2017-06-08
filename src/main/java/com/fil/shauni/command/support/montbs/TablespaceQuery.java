package com.fil.shauni.command.support.montbs;

import java.util.List;

/**
 *
 * @author Filippo
 */
public interface TablespaceQuery {
    String prepare(List<String> exclude, boolean undo, int pct_usage_threshold);
}
