package com.fil.shauni.command.export;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.CommaParameterSplitter;
import com.beust.jcommander.validators.PositiveInteger;
import com.fil.shauni.command.DatabaseCommandControl;
import com.fil.shauni.command.support.Exportable;
import com.fil.shauni.command.export.support.DatabaseQuery;
import com.fil.shauni.command.export.support.DatabaseTable;
import com.fil.shauni.command.support.SemicolonParameterSplitter;
import com.fil.shauni.command.support.worksplitter.WorkSplitter;
import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.util.Processor;
import com.fil.shauni.util.Sysdate;
import com.fil.shauni.util.file.DefaultFilepath;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import com.fil.shauni.util.file.Filepath;
import lombok.Getter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import static com.fil.shauni.util.GeneralUtil.*;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Filippo
 */
@Log4j2
public abstract class SpringExporter extends DatabaseCommandControl<String> {

    @Setter @Parameter(names = "-parallel", description = "parallelism Shauni will try to use to export data")
    protected int parallel = 1;

    @Setter @Parameter(names = "-tables", description = "list of tables to export from", splitter = CommaParameterSplitter.class, variableArity = true)
    protected List<String> tables;

    @Setter @Parameter(names = "-queries", description = "list of queries to fetch data from", splitter = SemicolonParameterSplitter.class, variableArity = true)
    protected List<String> queries;

    @Setter @Parameter(names = "-directory", description = "directory object to be used for dumpfiles")
    protected String directory = ".";

    @Setter @Parameter(names = "-filename", description = "destination dump files. Must use wildchar when multiple objects are exported")
    protected String filename = "%t-%d-%n[%w%u]";

    @Setter @Parameter(names = "-format", description = "format dump files (tab|csv)", required = false)
    protected String format = "tab";

    @Parameter(names = "-cluster", arity = 1, validateWith = PositiveInteger.class)
    protected Integer cluster = 1;

    private final List<String> sqlObjects = new ArrayList<>();

    @Getter
    private final WorkSplitter<String> workSplitter;

    private Exportable e;

    @Getter
    private final List<Processor<Filepath, WildcardContext>> replacers;

    private Map<Integer, List<String>> workersJobs;

    public SpringExporter(List<Processor<Filepath, WildcardContext>> replacers, WorkSplitter<String> workSplitter) {
        this.replacers = replacers;
        this.workSplitter = workSplitter;
    }

    @Override
    public boolean validate() {
        boolean result = false;
        if (tables == null && queries == null) {
            cli.print(() -> firstThread, (l, p) -> log.error(l, p), "({}) At least one parameters between queries and tables must be specified", _thread);
            return result;
        }
        if (tables != null && queries != null) {
            log.error("({}) Cannot use multiple modes together", _thread);
            cli.print(() -> firstThread, (l, p) -> log.error(l, p), "({}) Cannot use multiple modes together", _thread);
            return result;
        }
        if (tables != null) {
            e = new DatabaseTable();
        }
        if (queries != null) {
            e = new DatabaseQuery();
        }
        addAllIfNotNull(sqlObjects, tables, queries);
        if (parallel < 1) {
            log.error("({}) Parallel degree must be greater than zero", _thread);
            return result;
        }
        return true;
    }

    @Override
    public void setup() throws ShauniException {
        super.setup();
        this.workersJobs = workSplitter.splitWork(workers, sqlObjects);
        cli.print(() -> workers != parallel && firstThread, (l, p) -> log.info(l, p), "* Parallelism adjusted to {}\n", workers);
    }

    @Override
    protected void setDegree() {
        workers = Math.min(sqlObjects.size(), parallel);
    }

    @Override
    protected List<String> extractWorkSet(int i) {
        return workersJobs.get(i);
    }

    @Override
    protected void runTask(int w, int t, List<String> set) {
        final String obj = set.get(t);

        final String sql = e.convert(obj);
        final String out = e.display(obj);

        Filepath filepath = new DefaultFilepath(String.format("%s/%s", directory, filename));
        WildcardContext ctx = new WildcardContext(w, t, Sysdate.now(Sysdate.MINIMAL), out.replace("$", "\\$"), _thread);
        replacers.stream().reduce(replacers.get(0), (f, c) -> f.andThen(c)).process(filepath, ctx);
        cli.print((l, p) -> log.info(l), " . . (worker %d) exporting %s..", w, out);

        try {
            e.export(sql, jdbc, (ResultSetExtractor<Void>) rs -> {
                try {
                    final int r = write(rs, filepath);
                    if (r > 0) {
                        cli.print((l, p) -> log.info(l), "  -> exported %-55s%10d rows", out, r);
                    } else if (r == 0) {
                        cli.print((l, p) -> log.info(l), "  -> %s skipped because it is empty", out);
                        incrementErrorCount();
                    }
                } catch (IOException | SQLException e) {
                    log.error("Error while exporting to file {}\n -> {}", filepath.getFilepath(), e.getMessage());
                    incrementErrorCount();
                }
                return null;
            });
        } catch (DataAccessException e) {
            log.error("Error while fetching data:\n -> {}", e.getMessage());
            incrementErrorCount();
        }
    }

    protected abstract int write(final ResultSet rs, Filepath filename) throws SQLException, IOException;

    @Override
    public void takedownThread() {
        super.takedownThread();
        futures.clear();
    }

    @RequiredArgsConstructor @Getter
    public class WildcardContext {
        final int workerId, objectId;
        final String timestamp;
        final String table;
        final String threadName;
    }
    
    interface WildcardReplacer {
        void replace(Filepath in, WildcardContext context);
    }
}