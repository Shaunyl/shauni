package com.fil.shauni.command.export;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.CommaParameterSplitter;
import com.fil.shauni.command.export.support.Entity;
import com.fil.shauni.command.export.support.Query;
import com.fil.shauni.command.export.support.Table;
import com.fil.shauni.command.support.SemicolonParameterSplitter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.stream.Collectors.toList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Filippo
 */
@Log4j2 @Parameters(separators = "=")
public class ExportMode {

    @Setter @Parameter(names = "-tables", description = "list of tables to export",
            splitter = CommaParameterSplitter.class, variableArity = true, converter = TableConverter.class)
    private List<Entity> tables;

    @Setter @Parameter(names = "-queries", description = "list of queries to fetch data",
            splitter = SemicolonParameterSplitter.class, variableArity = true, converter = QueryConverter.class)
    private List<Entity> queries;

    private final static Map<Class<? extends Entity>, List<? extends Entity>> OBJECTS = new HashMap<>();

    @Getter
    private List<? extends Entity> sqlObjects;
    
    public boolean validate(boolean firstThread) {
        OBJECTS.put(Table.class, tables);
        OBJECTS.put(Query.class, queries);
        Collection<List<? extends Entity>> values = OBJECTS.values();

        long size = values.stream().filter(Objects::nonNull).count();
        if (size > 1) {
            if (firstThread) {
                log.error("Cannot use multiple modes together");
            }
            return false;
        }
        if (size == 0) {
            if (firstThread) {
                log.error("At least one mode must be specified");
            }
            return false;
        }
        sqlObjects = values.stream().filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(toList());
        return true;
    }
}
