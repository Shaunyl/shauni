package com.fil.shauni.command.export;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.beust.jcommander.validators.PositiveInteger;
import com.fil.shauni.command.CommandStatus;
import com.fil.shauni.command.DatabaseCommandControl;
import com.fil.shauni.command.export.support.*;
import com.fil.shauni.command.support.worksplitter.WorkSplitter;
import com.fil.shauni.concurrency.pool.ThreadPoolManager;
import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.util.*;
import com.fil.shauni.util.file.spi.DefaultFilepath;
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
import java.util.concurrent.*;
import lombok.RequiredArgsConstructor;

/**
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@Log4j2
public abstract class SpringExporter extends DatabaseCommandControl implements Parallelizable {

    @Setter @Parameter(names = "-parallel", description = "number of worker used to split the workload")
    private int parallel = 1;

    @Setter @Parameter(names = "-directory", description = "destination of dumpfiles")
    private String directory = ".";

    @Setter @Parameter(names = "-filename", description = "name of dumpfiles")
    private String filename = "%t-%d-%n[%w%u]";

    @Setter @Parameter(names = "-format", description = "format of dumpfiles")
    private String format = "tab";

    @ParametersDelegate @Getter
    private final ExportMode mode = new ExportMode();

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
        boolean result = mode.validate(firstThread);
        if (parallel < 1) {
            status.error();
            log.error("Parallelism degree must be greater than zero");
            return false;
        }
        if (!result) {
            status.error();
        }
        return result;
    }

    @Override
    public void setup() {
        super.setup();
        List<? extends Entity> sqlObjects = mode.getSqlObjects();
        int size = sqlObjects.size();
        log.info("* Found {} object(s) to export", size);
        log.info("* Format used: {}", format);
        if (parallel > size) {
            parallel = size;
            cli.print(firstThread, (l, p) -> log.info(l, p), "* Parallelism adjusted to {}", parallel);
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
                status.error();
                status.setState(CommandStatus.State.ABORTED);
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
            status.error();
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
                    cli.print(r > 0, (l, p) -> log.info(l), "  -> exported %-55s%10d rows", out, r);
                    cli.print(r == 0, (l, p) -> log.info(l), "  -> %s skipped because it is empty", out);
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
    public static class WildcardContext {
        final int workerId, objectId;

        final String timestamp;

        final String table;

        final String threadName;
    }

    private interface WildcardReplacer {
        void replace(Filepath in, WildcardContext context);
    }
}