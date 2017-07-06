package com.fil.shauni.command.montbs;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.CommaParameterSplitter;
import com.beust.jcommander.internal.Lists;
import com.fil.shauni.command.DatabaseCommandControl;
import com.fil.shauni.command.support.CharConverter;
import com.fil.shauni.command.support.UpperCaseConverter;
import com.fil.shauni.command.support.montbs.AbstractDatabaseQueryFactory;
import com.fil.shauni.command.support.montbs.DatabaseQueryFactory;
import com.fil.shauni.command.support.montbs.MonAutoTablespaceQuery;
import com.fil.shauni.command.support.montbs.MonTablespaceQuery;
import com.fil.shauni.command.support.montbs.TablespaceQuery;
import com.fil.shauni.util.file.spi.DefaultFilepath;
import com.fil.shauni.util.Sysdate;
import com.fil.shauni.util.file.Filepath;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@Log4j2
public abstract class DefaultMonTbs extends DatabaseCommandControl {

    @Getter @Parameter(names = "-directory", description = "working directory", arity = 1)
    protected String directory = ".";

    @Getter @Parameter(names = "-critical", description = "critical threshold", arity = 1)
    protected Integer critical = 95;

    @Getter @Parameter(names = "-warning", description = "warning threshold", arity = 1)
    protected Integer warning = 85;

    @Getter @Parameter(names = "-undo", description = "if enabled, retrieve also UNDO tablespaces")
    protected boolean undo;

    @Getter @Parameter(names = "-auto", description = "retrieve info considering also autoextensible datafiles")
    protected boolean autoextend;

    @Getter @Parameter(names = "-unit", description = "tablespace space unit", converter = CharConverter.class)
    protected char unit = 'h';

    @Getter @Parameter(names = "-growing", description = "if enabled, shows growing tablespaces since last run")
    protected boolean growing;
    
    @Getter @Parameter(names = "-persist", description = "if enabled, save data to the local database")
    protected boolean persist;

    @Getter @Parameter(names = "-exclude", description = "list of tablespaces to exclude", 
            splitter = CommaParameterSplitter.class,
            variableArity = true, converter = UpperCaseConverter.class)
    protected final List<String> exclude = Lists.newArrayList();

    private final static Map<Boolean, Class<? extends TablespaceQuery>> QUERIES = new HashMap<>();

    private final AbstractDatabaseQueryFactory factory = new DatabaseQueryFactory();

    private String query;

    @Override
    public boolean validate() {
        if ((warning < 1 || critical < 1) || (warning > 99 || critical > 99)) {
            cli.print((l, p) -> log.error(l), "Threshold parameters must be between 1 to 99.");
            return false;
        }
        if (warning >= critical) {
            cli.print((l, p) -> log.error(l), "Critical threshold must be greater than warning one.");
            return false;
        }
        return true;
    }

    @Override
    public void setup() {
        super.setup();
        QUERIES.put(true, MonAutoTablespaceQuery.class); // FIXME: 1. move out; 2. only two keys would be possible (limit)
        QUERIES.put(false, MonTablespaceQuery.class);
        TablespaceQuery q = factory.create(QUERIES.get(autoextend));

        query = q.prepare(exclude, undo, warning);
        cli.print(undo, (l, p) -> log.info(l), "* UNDO tablespaces included");
        cli.print(autoextend, (l, p) -> log.info(l), "* Autoextend mode enabled: autoextend datafiles taken into account");
        int size = exclude.size();
        cli.print(size > 0, (l, p) -> log.info(l, p), "* List of tablespaces to exclude:\n  -> {}", String.join(", ", exclude));
        cli.print(size == 0, (l, p) -> log.info(l), "* No tablespaces to exclude");
        cli.print((l, p) -> log.info(l, p), "* Thresholds are: warning ({}), critical ({})", warning, critical);
        cli.print(growing, (l, p) -> log.info(l, p), "* Growing check enabled");

        cli.print(firstThread, (l, p) -> log.debug(l, p), "> query to execute:\n{}", query.replaceAll("(?m)^", "  "));
    }

    @Override
    public void run(int sid) {
        super.run(sid);
        runWorker(1);
    }

    @Override
    public void runJob(int w) throws Exception {
        String host = databasePoolManager.getHost();
        String sid = databasePoolManager.getSid();
        String filename = String.format("MONTBS-%s-%s.txt", sid, Sysdate.now(Sysdate.SQUELCHED_TIMEDATE));

        Filepath filepath = new DefaultFilepath(String.format("%s/%s", directory, filename));
        log.info("\n[{}@{}] Output file will be:\n   {}\n", sid, host, filepath.getFilepath());
        jdbc.query(query, (ResultSetExtractor<Void>) rs -> {
            try {
                log.info("Working on...");
                write(rs, filepath);
            } catch (IOException | SQLException e) {
                cli.print((l, p) -> log.error(l, p), "  -> Error while writing to file {}\n{}", filepath.getFilepath(), e.getMessage());
            }
            return null;
        });
        cli.print((l, p) -> log.info(l), "  -> Report generated successfully\n");
    }

    protected abstract int write(final ResultSet rs, Filepath filename) throws SQLException, IOException;

    @Override
    public void takedown() {
        super.takedown();
    }
}
