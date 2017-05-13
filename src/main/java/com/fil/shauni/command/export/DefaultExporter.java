package com.fil.shauni.command.export;

import com.fil.shauni.command.support.Validator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.CommaParameterSplitter;
import com.beust.jcommander.validators.PositiveInteger;
import com.fil.shauni.Main;
import com.fil.shauni.command.support.WorkSplitter;
import com.fil.shauni.concurrency.pool.ThreadPoolManager;
import com.fil.shauni.command.DatabaseCommandControl;
import com.fil.shauni.command.Check;
import com.fil.shauni.command.export.support.*;
import com.fil.shauni.command.support.Context;
import com.fil.shauni.command.support.SemicolonParameterSplitter;
import com.fil.shauni.command.support.StatementManager;
import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.log.LogLevel;
import com.fil.shauni.util.DateFormat;
import com.fil.shauni.util.GeneralUtil;
import com.fil.shauni.util.file.DefaultFilename;
import com.fil.shauni.util.file.Filename;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import javax.inject.Inject;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Shaunyl
 */
@Log4j2 @NoArgsConstructor @Component("exp-off") @Scope("prototype")
public abstract class DefaultExporter extends DatabaseCommandControl implements Exporter {

    @Setter @Parameter(names = "-parallel", description = "parallelism Shauni will try to use to export data")
    protected int parallel = 1;

    @Setter @Parameter(names = "-tables", description = "list of tables to export from", splitter = CommaParameterSplitter.class, variableArity = true)
    protected List<String> tables;

    @Setter @Parameter(names = "-queries", description = "list of queries to fetch data from", splitter = SemicolonParameterSplitter.class, variableArity = true)
    protected List<String> queries;

    @Setter @Parameter(names = "-directory", description = "directory object to be used for dumpfiles")
    protected String directory = ".";

    @Setter @Parameter(names = "-filename", description = "destination dump files. Must use wildchar when multiple objects are exported")
    protected String filename = "%i-%h_%d_%u";

    @Setter @Parameter(names = "-format", description = "format dump files (tab|csv)", required = false)
    protected String format = "tab";

    @Parameter(names = "-cluster", arity = 1, validateWith = PositiveInteger.class)
    public Integer cluster = 1;

    private int batchSize = 0, adjustedParallel;

    private ArrayList<String> objects;

    private final List<FutureTask<Void>> futures = new ArrayList<>();

    private Set<WildcardReplacer> replacers;

    private final static String LOG_NOTE = "  Note:";

    @Inject
    private StatementManager statementManager;

    @Inject
    private WorkSplitter workSplitter;

    @Inject
    private final Validator<String> validator = new ExportValidator<>();

    private ExportMode exportMode;

    private static final int FETCH_SIZE = 100;

    public DefaultExporter(String name) {
        this(name, new HashSet<WildcardReplacer>() {
            {
                add(Main.beanFactory.getBean(WWildcardReplacer.class));
                add(Main.beanFactory.getBean(UWildcardReplacer.class));
                add(Main.beanFactory.getBean(DWildcardReplacer.class));
                add(Main.beanFactory.getBean(NWildcardReplacer.class));
            }
        });
    }

    public DefaultExporter(String name, Set<WildcardReplacer> replacers) {
        this.replacers = replacers;
        this.name = name;
        super.isCluster = true;
    }

    @Override
    public Check validate() throws ShauniException {
        this.objects = validator.validate(tables, queries);
        if (tables != null) {
            this.exportMode = new TableExportMode();
            this.replacers.add(Main.beanFactory.getBean(TWildcardReplacer.class));
        }
        if (queries != null) {
            this.exportMode = new QueryExportMode();
            if (filename.contains("%t")) {
                commandLinePresentation.printIf(firstThread, LogLevel.INFO, "{} wildcard %%t is not supported in Query Mode", LOG_NOTE);
            }
        }
        
        if (parallel < 1) {
            throw new ShauniException(1001, "Parallel degree must be greater than zero [" + currentThreadName + "]");
        }
        return new Check();
    }

    @Override
    public void setup() throws ShauniException {
        super.setup();
        this.executorService = ThreadPoolManager.getInstance();
        this.batchSize = objects.size();
        this.adjustedParallel = batchSize <= parallel ? batchSize : parallel;
        if (adjustedParallel > 1) {
            commandLinePresentation.printIf(firstThread, LogLevel.INFO, "%s parallelism enabled (requested: %d, adjusted: %d)\n", LOG_NOTE, parallel, adjustedParallel);
        }
    }

    @Override
    public Long call() throws Exception {
        return super.call();
    }

    @Override
    public void run() throws ShauniException {
        Map<Integer, String[]> workersJobs = workSplitter.splitWork(adjustedParallel, objects);
        for (int w = 0; w < this.adjustedParallel; w++) {
            this.runMultiple(w, workersJobs.get(w));
        }

        for (int w = 0; w < this.adjustedParallel; w++) {
            FutureTask<Void> future = futures.get(w);
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new ShauniException(1004, String.format("Thread %d interrupted abnormally..\n --> %s", w, e.getMessage()));
            }
        }
    }

    private void runMultiple(final int worker, final Object[] set) {
        log.debug("worker " + worker + " has been submitted");
        FutureTask<Void> future = new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws ShauniException {
                try {
                    export(worker, set);
                } catch (ShauniException e) {
                    throw new ShauniException(1020, e.getMessage());
                }
                return null;
            }
        });
        futures.add(future);
        this.executorService.execute(future);
    }

    @Override
    public void export(final int workerId, final Object[] set) throws ShauniException {
        log.debug("worker {} is preparing for the export", workerId);
        
        Connection connection = super.getConnection(workerId);
        if (connection == null) {
            return;
        }
        
        log.info("Worker {} connected to {}@{} at {}", workerId, databasePoolManager.getSid(), databasePoolManager.getHost(), GeneralUtil.getCurrentDate(DateFormat.TIMEONLY));

        Statement statement = statementManager.createStatement(connection, FETCH_SIZE);
        for (int objectId = 0; objectId < set.length; objectId++) {
            String obj = set[objectId].toString().toUpperCase();
            String sql = this.exportMode.rearrangeSQL(obj);
            ResultSet rs;
            try {
                log.debug("Executing query.. {}", sql);
                rs = statement.executeQuery(sql);
            } catch (SQLException e) {
                errorCount++;
                log.debug("error executing query");
                commandLinePresentation.print(LogLevel.WARN, "Warning: object %s skipped\n  -> %s", obj, e.getMessage());
                continue;
            }

            if (rs == null) {
                errorCount++;
                log.debug("error: Result Set is null");
                commandLinePresentation.print(LogLevel.WARN, " . . (worker %d) warning while exporting %-40s\n  -> Result Set is null", workerId, obj);
                continue;
            }

            String path = String.format("%s/%s", directory, filename);

            Filename fn = new DefaultFilename(path, filename);

            String timestamp = GeneralUtil.getCurrentDate(DateFormat.CLEAN_DATETIME); // FIXME: Should be modifiable from client..
            Context ctx = new Context(workerId, objectId, timestamp, obj, currentThreadName);

            for (WildcardReplacer replacer : replacers) {
                fn = replacer.replace(fn, ctx);
            }

            log.debug("Output directory is: {}", directory);
            commandLinePresentation.print(LogLevel.INFO, " . . (worker %d) exporting %-40s", workerId, exportMode.getShortName(obj) + "..");
            int rows = 0;
            try {
                rows = write(rs, fn);
            } catch (IOException ex) {
                throw new ShauniException(1005, "Error while writing data to the file " + filename + "\n -> " + ex.getMessage());
            } catch (SQLException ex) {
                throw new ShauniException(1006, "Error while reading the result set\n -> " + ex.getMessage());
            }
            commandLinePresentation.print(LogLevel.INFO, "  -> %s %-60s%10d%5s", "exported", exportMode.getName(obj), rows, " rows");
        }
    }

    protected abstract int write(final ResultSet rs, Filename filename) throws SQLException, IOException;

    @Override
    public void takedown() {
        super.takedown();
//        ThreadPoolManager.shutdownPool();
        futures.clear();
    }
}
