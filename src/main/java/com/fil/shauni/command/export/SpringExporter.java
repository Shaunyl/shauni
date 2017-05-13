package com.fil.shauni.command.export;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.CommaParameterSplitter;
import com.beust.jcommander.validators.PositiveInteger;
import com.fil.shauni.Configuration;
import com.fil.shauni.Main;
import com.fil.shauni.command.Check;
import com.fil.shauni.command.DatabaseCommandControl;
import com.fil.shauni.command.export.support.DWildcardReplacer;
import com.fil.shauni.command.export.support.NWildcardReplacer;
import com.fil.shauni.command.export.support.TWildcardReplacer;
import com.fil.shauni.command.export.support.UWildcardReplacer;
import com.fil.shauni.command.export.support.WWildcardReplacer;
import com.fil.shauni.command.export.support.WildcardReplacer;
import com.fil.shauni.command.support.Context;
import com.fil.shauni.command.support.SemicolonParameterSplitter;
import com.fil.shauni.command.support.Validator;
import com.fil.shauni.command.support.WorkSplitter;
import com.fil.shauni.concurrency.pool.ThreadPoolManager;
import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.log.LogLevel;
import com.fil.shauni.util.DateFormat;
import com.fil.shauni.util.GeneralUtil;
import com.fil.shauni.util.file.DefaultFilename;
import com.fil.shauni.util.file.Filename;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Chiara
 */
@Log4j2 @NoArgsConstructor @Component("exp") @Scope("prototype")
public abstract class SpringExporter extends DatabaseCommandControl implements Exporter {

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

    private final static String LOG_NOTE = "  Note:";

    @Inject
    private WorkSplitter workSplitter;

    @Inject
    private final Validator<String> validator = new ExportValidator<>();

    private ExportMode exportMode;

    private Set<WildcardReplacer> replacers;

    public SpringExporter(String name) {
        this(name, new HashSet<WildcardReplacer>() {
            {
                add(Main.beanFactory.getBean(WWildcardReplacer.class));
                add(Main.beanFactory.getBean(UWildcardReplacer.class));
                add(Main.beanFactory.getBean(DWildcardReplacer.class));
                add(Main.beanFactory.getBean(NWildcardReplacer.class));
            }
        });
    }

    public SpringExporter(String name, Set<WildcardReplacer> replacers) {
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
                e.printStackTrace();
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
    public void export(int workerId, Object[] set) throws ShauniException {
        log.debug("worker {} is preparing for the export", workerId);
        for (int objectId = 0; objectId < set.length; objectId++) {
            String obj = set[objectId].toString().toUpperCase();
            String sql = this.exportMode.rearrangeSQL(obj);

            String path = String.format("%s/%s", directory, filename);
            Filename fn = new DefaultFilename(path, filename);

            // FIXME: Give the possibility to choose the date format to the clients
            String timestamp = GeneralUtil.getCurrentDate(DateFormat.CLEAN_DATETIME); // FIXME: Should be modifiable from client..
            Context ctx = new Context(workerId, objectId, timestamp, obj, currentThreadName);

            for (WildcardReplacer replacer : replacers) {
                fn = replacer.replace(fn, ctx);
            }

            commandLinePresentation.printIf(firstThread && objectId == 0 && workerId == 0, LogLevel.DEBUG, "Output directory is: %s/%s", Configuration.ROOT_DIRECTORY, directory);
            commandLinePresentation.print(LogLevel.INFO, " . . (worker %d) exporting %s@%-40s", workerId, exportMode.getShortName(obj), databasePoolManager.getHost() + "..");

            int rows = jdbc.query(sql, new ExporterExtractor(this, fn));
            commandLinePresentation.print(LogLevel.INFO, "  -> %s %-60s%10d%5s", "exported", exportMode.getName(obj) + "@" + databasePoolManager.getHost(), rows, " rows");
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
