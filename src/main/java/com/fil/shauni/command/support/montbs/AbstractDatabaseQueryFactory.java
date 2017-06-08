package com.fil.shauni.command.support.montbs;

/**
 *
 * @author Filippo
 */
public abstract class AbstractDatabaseQueryFactory {
    public abstract TablespaceQuery create(Class<? extends TablespaceQuery> clazz);
}
