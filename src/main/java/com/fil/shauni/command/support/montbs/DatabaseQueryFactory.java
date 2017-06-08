package com.fil.shauni.command.support.montbs;

import com.fil.shauni.exception.ShauniException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * @author Filippo
 */
public class DatabaseQueryFactory extends AbstractDatabaseQueryFactory {

    private final static Map<Class<? extends TablespaceQuery>, Supplier<? extends TablespaceQuery>> QUERIES = new HashMap<>();

    static {
        QUERIES.put(MonTablespaceQuery.class, MonTablespaceQuery::new);
        QUERIES.put(MonAutoTablespaceQuery.class, MonAutoTablespaceQuery::new);
    }

    @Override
    public TablespaceQuery create(Class<? extends TablespaceQuery> clazz) {
        TablespaceQuery s = QUERIES.get(clazz).get();
        if (s != null) {
            return s;
        }
        throw new ShauniException(600, "Query unknown");
    }
}
