package com.fil.shauni.command.export;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.CommaParameterSplitter;
import com.beust.jcommander.validators.PositiveInteger;
import com.fil.shauni.command.DatabaseCommandControl;
import com.fil.shauni.command.support.Exportable;
import com.fil.shauni.command.export.support.DatabaseQuery;
import com.fil.shauni.command.export.support.DatabaseTable;
import com.fil.shauni.command.export.support.Parallelizable;
import com.fil.shauni.command.support.SemicolonParameterSplitter;
import com.fil.shauni.command.support.worksplitter.WorkSplitter;
import com.fil.shauni.concurrency.pool.ThreadPoolManager;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Filippo
 */
@Log4j2
public abstract class SpringExporter extends DatabaseCommandControl implements Parallelizable {

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

    private Map<Integer, List<String>> workSet;

    private final List<FutureTask<Void>> futures = new ArrayList<>();

    private int tid;
    
    private ExecutorService executorService = ThreadPoolManager.getInstance();

    public SpringExporter(List<Processor<Filepath, WildcardContext>> replacers, WorkSplitter<String> workSplitter) {
        this.replacers = replacers;
        this.workSplitter = workSplitter;
    }

    @Override
    public boolean validate() {
        this.tid = configuration.getTid();
        boolean result = false;
        if (tables == null && queries == null) {
            cli.print(() -> firstThread, (l, p) -> log.error(l, p), "({}) At least one parameters between queries and tables must be specified", tid);
            return result;
        }
        if (tables != null && queries != null) {
            log.error("({}) Cannot use multiple modes together", tid);
            cli.print(() -> firstThread, (l, p) -> log.error(l, p), "({}) Cannot use multiple modes together", tid);
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
            log.error("({}) Parallel degree must be greater than zero", tid);
            return result;
        }
        return true;
    }

    @Override
    public void setup() {
        super.setup();
        if (parallel > sqlObjects.size()) {
            parallel = sqlObjects.size();
            cli.print(() -> firstThread, (l, p) -> log.info(l, p), "* Parallelism adjusted to {}\n", parallel);
        }
        this.workSet = workSplitter.splitWork(parallel, sqlObjects);
    }

    @Override
    public void run(int sid) {
        super.run(sid);
        runWorker();
        get();
    }

    @Override
    public void get() {
        for (int worker = 0; worker < configuration.getParallel(); worker++) {
            FutureTask<Void> future = futures.get(worker);
            try {
                future.get();
                log.debug("getResult worker {}", worker);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new RuntimeException(String.format("Worker %d interrupted abnormally..\n --> %s", worker, e.getMessage()));
            }
        }
    }

    @Override
    public void runJob(int worker) {
        FutureTask<Void> future = new FutureTask<>(() -> {
            log.debug("worker {} is preparing for the export", worker);
            List<String> jobSet = workSet.get(worker);
            for (int i = 0; i < jobSet.size(); i++) {
                this.export(worker, i, jobSet);
            }
            log.debug("worker {} has terminated", worker);
        }, null);
        this.futures.add(future);
        this.executorService.execute(future);
    }

    void export(int w, int t, List<String> set) {
        final String obj = set.get(t);

        final String sql = e.convert(obj);
        final String out = e.display(obj);

        Filepath filepath = new DefaultFilepath(String.format("%s/%s", directory, filename));
        WildcardContext ctx = new WildcardContext(w, t, Sysdate.now(Sysdate.MINIMAL), out.replace("$", "\\$"), configuration.getTname());
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
                    }
                } catch (IOException | SQLException e) {
                    log.error("Error while exporting to file {}\n -> {}", filepath.getFilepath(), e.getMessage());
                }
                return null;
            });
        } catch (DataAccessException e) {
            log.error("Error while fetching data:\n -> {}", e.getMessage());
        }
    }

    protected abstract int write(final ResultSet rs, Filepath filename) throws SQLException, IOException;

    @Override
    public void takedown() {
        super.takedown();
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