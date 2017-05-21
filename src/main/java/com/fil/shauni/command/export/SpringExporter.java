package com.fil.shauni.command.export;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.CommaParameterSplitter;
import com.beust.jcommander.validators.PositiveInteger;
import com.fil.shauni.Main;
import com.fil.shauni.command.Check;
import com.fil.shauni.command.DatabaseCommandControl;
import com.fil.shauni.command.export.support.*;
import com.fil.shauni.command.support.Context;
import com.fil.shauni.command.support.SemicolonParameterSplitter;
import com.fil.shauni.command.support.WorkSplitter;
import com.fil.shauni.concurrency.pool.ThreadPoolManager;
import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.util.BooleanUtils;
import com.fil.shauni.util.Sysdate;
import com.fil.shauni.util.WildcardContext;
import com.fil.shauni.util.file.DefaultFilepath;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.fil.shauni.util.file.Filepath;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 *
 * @author Chiara
 */
@Log4j2 @NoArgsConstructor @Component("exp") @Scope("prototype")
public abstract class SpringExporter extends DatabaseCommandControl implements Exporter {

    @Setter @Parameter(names = "-parallel", description = "parallelism Shauni will try to use to export data")
    protected int parallel = 1;

    @Setter @Parameter(names = "-tables", description = "list of tables to export from", converter = TableObjectConverter.class, splitter = CommaParameterSplitter.class, variableArity = true)
    protected List<ExporterObject> tables;

    @Setter @Parameter(names = "-queries", description = "list of queries to fetch data from", converter = QueryObjectConverter.class, splitter = SemicolonParameterSplitter.class, variableArity = true)
    protected List<ExporterObject> queries;

    @Setter @Parameter(names = "-directory", description = "directory object to be used for dumpfiles")
    protected String directory = ".";

    @Setter @Parameter(names = "-filename", description = "destination dump files. Must use wildchar when multiple objects are exported")
    protected String filename = "%i-%h_%d_%u";

    @Setter @Parameter(names = "-format", description = "format dump files (tab|csv)", required = false)
    protected String format = "tab";

    @Parameter(names = "-cluster", arity = 1, validateWith = PositiveInteger.class)
    public Integer cluster = 1;

    private int adjParallel;

    private List<ExporterObject> sqlObjects;

    private final List<FutureTask<Void>> futures = new ArrayList<>();

    @Inject
    private WorkSplitter<ExporterObject> workSplitter;

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

    @Override @SuppressWarnings("unchecked")
    public Check validate() throws ShauniException {
        sqlObjects = BooleanUtils.xor(tables, queries);
        if (sqlObjects == null) {
            if (tables == null) {
                return new Check(1040, "At least one parameter bewteen -queries and -tables must be specified");
            }
            return new Check(1041, "Parameters -queries and -tables are mutually exclusive");
        }
        if (tables != null) {
            this.replacers.add(Main.beanFactory.getBean(TWildcardReplacer.class));
        } else {
            if (filename.contains("%t")) {
                cli.print(() -> firstThread, (l, p) -> log.info(l), "* Wildcard %%t not supported in query mode");
            }
        }
        if (parallel < 1) {
            return new Check(1042, "Parallel degree must be greater than zero [" + currentThreadName + "]");
        }
        return new Check();
    }

    @Override
    public void setup() throws ShauniException {
        super.setup();
        this.executorService = ThreadPoolManager.getInstance();
        this.adjParallel = Math.min(sqlObjects.size(), parallel);
        cli.print(() -> adjParallel > 1 && firstThread, (l, p) -> log.info(l, p), "* Parallelism enabled (requested: {}, adjusted: {})\n", parallel, adjParallel);
    }

    @Override
    public void run() throws ShauniException {
        Map<Integer, ExporterObject[]> workersJobs = workSplitter.splitWork(ExporterObject.class, adjParallel, sqlObjects);
        for (int w = 0; w < this.adjParallel; w++) {
            this.runMultiple(w, workersJobs.get(w));
        }

        for (int w = 0; w < this.adjParallel; w++) {
            FutureTask<Void> future = futures.get(w);
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new ShauniException(1004, String.format("Thread %d interrupted abnormally..\n --> %s", w, e.getMessage()));
            }
        }
    }

    private void runMultiple(final int worker, final ExporterObject[] set) {
        FutureTask<Void> future = new FutureTask<>(() -> {
            try {
                cli.print((l, p) -> log.debug(l, p), "worker {} is preparing for the export", worker);
                for (int objectId = 0; objectId < set.length; objectId++) {
                    export(worker, objectId, set);
                }
                cli.print((l, p) -> log.debug(l, p), "worker {} has terminated", worker);
            } catch (ShauniException e) {
                throw new ShauniException(1020, e.getMessage());
            }
            return null;
        });
        futures.add(future);
        this.executorService.execute(future);
    }

    @Override
    public void export(final int workerId, final int objectId, ExporterObject[] set) throws ShauniException {
        ExporterObject obj = set[objectId];
        Filepath filepath = new DefaultFilepath(String.format("%s/%s", directory, filename));

        String timestamp = Sysdate.now(Sysdate.MINIMAL);
        Context ctx = new Context(workerId, objectId, timestamp, obj, currentThreadName);
        replacers.forEach((WildcardReplacer replacer) -> replacer.replace(filepath, ctx));

        cli.print((l, p) -> log.info(l), " . . (worker %d) exporting %s..", workerId, obj.display());
        Integer rows = jdbc.query(obj.sql(), (ResultSetExtractor<Integer>) rs -> {
            try {
                return write(rs, filepath);
            } catch (IOException e) {
                cli.print((l, p) -> log.error(l, p), "Error while writing data to the file {}\n -> {}", filepath.getFilepath(), e.getMessage());
                return -1;
            }
        });
        cli.print(() -> 0 < rows, (l, p) -> log.info(l), "  -> exported %-55s%10d rows", obj.display(), rows);
    }

    protected abstract int write(final ResultSet rs, Filepath filename) throws SQLException, IOException;

    @Override
    public void takedown() {
        super.takedown();
        futures.clear();
    }
}
