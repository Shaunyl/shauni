package com.fil.shauni.command.export;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.CommaParameterSplitter;
import com.fil.shauni.command.DatabaseCommandControl;
import com.fil.shauni.command.export.support.*;
import com.fil.shauni.command.support.SemicolonParameterSplitter;
import com.fil.shauni.command.support.worksplitter.WorkSplitter;
import com.fil.shauni.concurrency.pool.ThreadPoolManager;
import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.util.*;
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
import java.util.HashMap;
import java.util.Objects;
import lombok.Getter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import java.util.concurrent.*;
import lombok.RequiredArgsConstructor;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@Log4j2
public abstract class SpringExporter extends DatabaseCommandControl implements Parallelizable {

    @Setter @Parameter(names = "-parallel", description = "number of worker used to split the workload")
    private int parallel = 1;

    @Setter @Parameter(names = "-tables", description = "list of tables to export",
            splitter = CommaParameterSplitter.class, variableArity = true,
            converter = TableConverter.class)
    private List<Entity> tables;

    @Setter @Parameter(names = "-queries", description = "list of queries to fetch data",
            splitter = SemicolonParameterSplitter.class, variableArity = true,
            converter = QueryConverter.class)
    private List<Entity> queries;

    @Setter @Parameter(names = "-directory", description = "destination of dumpfiles")
    private String directory = ".";

    @Setter @Parameter(names = "-filename", description = "name of dumpfiles")
    private String filename = "%t-%d-%n[%w%u]";

    @Setter @Parameter(names = "-format", description = "format of dumpfiles")
    private String format = "tab";

    private List<? extends Entity> sqlObjects;

    private final static Map<Class<? extends Entity>, List<? extends Entity>> OBJECTS = new HashMap<>();

    @Getter
    private final WorkSplitter<String> workSplitter;

    @Getter
    private final List<Processor<Filepath, WildcardContext>> replacers;

    private Map<Integer, ? extends List<? extends Entity>> workSet;

    private final List<FutureTask<Void>> futures = new ArrayList<>();

    private final ExecutorService executorService = ThreadPoolManager.getInstance();

    @SuppressWarnings("unchecked")
    public SpringExporter(List<Processor<Filepath, WildcardContext>> replacers, WorkSplitter<String> workSplitter) {
        this.replacers = replacers;
        this.workSplitter = workSplitter;
    }

    @Override
    public boolean validate() {
        OBJECTS.put(Table.class, tables);
        OBJECTS.put(Query.class, queries);
        
        Stream<List<? extends Entity>> filter = OBJECTS.values().stream().filter(Objects::nonNull);
        long size = filter.count();
        if (size > 1) {
            cli.print(() -> firstThread, (l, p) -> log.error(l, p), "Cannot use multiple modes together");
            return false;
        }
        if (size == 0) {
            sqlObjects = filter.flatMap(List::stream).collect(toList());
            cli.print(() -> firstThread, (l, p) -> log.error(l, p), "At least one mode must be specified");
            return false;
        }
        if (parallel < 1) {
            log.error("Parallel degree must be greater than zero");
            return false;
        }
        return true;
    }

    @Override
    public void setup() {
        super.setup();
        int size = sqlObjects.size();
        log.info("* Found {} object(s) to export.", size);
        log.info("* Format used: {}", format);
        if (parallel > size) {
            parallel = size;
            cli.print(() -> firstThread, (l, p) -> log.info(l, p), "* Parallelism adjusted to {}", parallel);
        }
        workSet = workSplitter.splitWork(parallel, sqlObjects);
    }

    @Override
    public void run(int sid) {
        super.run(sid);
        runWorker(parallel);
        get();
    }

    @Override
    public void get() {
        for (int worker = 0; worker < parallel; worker++) {
            FutureTask<Void> future = futures.get(worker);
            try {
                future.get();
                log.debug("getResult worker {}", worker);
            } catch (InterruptedException | ExecutionException e) {
                throw new ShauniException(String.format("Worker %d interrupted abnormally..\n --> %s", worker, e.getMessage()));
            }
        }
    }

    @Override
    public void runJob(int worker) {
        FutureTask<Void> future = new FutureTask<>(() -> {
            log.debug("worker {} is preparing for the export", worker);
            List<? extends Entity> jobSet = workSet.get(worker);
            for (int i = 0; i < jobSet.size(); i++) {
                this.export(worker, i, jobSet);
            }
            log.debug("worker {} has terminated", worker);
        }, null);
        this.futures.add(future);
        this.executorService.execute(future);
    }

    void export(int w, int t, List<? extends Entity> set) {
        Entity entity = set.get(t);
        String obj = entity.getObj();

        final String sql = entity.convert(obj);
        if (sql == null) {
            log.error(" -> '{}' has been skipped.", obj);
            return;
        }
        final String out = entity.display(obj).trim();

        Filepath filepath = new DefaultFilepath(String.format("%s/%s", directory, filename));
        WildcardContext ctx = new WildcardContext(w, t, Sysdate.now(Sysdate.MINIMAL), out, configuration.getTname());
        replacers.stream().reduce(replacers.get(0), (f, c) -> f.andThen(c)).process(filepath, ctx);
        cli.print((l, p) -> log.info(l), " . . (worker %d) exporting %s..", w, out);

        try {
            entity.export(sql, jdbc, (ResultSetExtractor<Void>) rs -> {
                try {
                    final int r = write(rs, filepath);
                    if (r > 0) {
                        cli.print((l, p) -> log.info(l), "  -> exported %-55s%10d rows", out, r);
                    } else if (r == 0) {
                        cli.print((l, p) -> log.info(l), "  -> %s skipped because it is empty", out);
                    }
                } catch (IOException | SQLException e) {
                    status.error();
                    log.error("Error while exporting to file {}\n -> {}", filepath.getFilepath(), e.getMessage());
                }
                return null;
            });
        } catch (DataAccessException e) {
            status.error();
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

    private interface WildcardReplacer {
        void replace(Filepath in, WildcardContext context);
    }
}