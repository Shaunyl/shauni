package com.fil.shauni.command.montbs;

import com.fil.shauni.command.support.Query;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.CommaParameterSplitter;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.validators.PositiveInteger;
import com.fil.shauni.command.DatabaseCommandControl;
import com.fil.shauni.command.support.CharBooleanValidator;
import com.fil.shauni.log.LogLevel;
import com.fil.shauni.command.CommandConfiguration;
import com.fil.shauni.mainframe.ui.CommandLinePresentation;
import com.fil.shauni.util.file.DefaultFilepath;
import com.fil.shauni.util.StringUtils;
import com.fil.shauni.util.Sysdate;
import com.fil.shauni.util.file.Filepath;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 *
 * @author Shaunyl
 */
@Log4j2
public abstract class DefaultMonTbs extends DatabaseCommandControl {

    @Parameter(names = "-directory", arity = 1)
    protected String directory = ".";

    @Parameter(names = "-critical", arity = 1, validateWith = PositiveInteger.class)
    public Integer critical = 95;

    @Parameter(names = "-warning", arity = 1, validateWith = PositiveInteger.class)
    public Integer warning = 85;

    @Parameter(names = "-undo", validateWith = CharBooleanValidator.class)
    public String undo = "y";

    @Parameter(names = "-exclude", splitter = CommaParameterSplitter.class, variableArity = true)
    protected List<String> exclude = Lists.newArrayList();

    @Parameter(names = "-cluster", arity = 1, validateWith = PositiveInteger.class)
    public Integer cluster = 1;

    @Inject
    private CommandLinePresentation commandLinePresentation;

    private final String COMMENT = "--";

    private String query;

    @Override
    public boolean validate() {
        boolean result = false;
        if ((warning | critical) < 1 || (warning | critical) > 99) {
            log.error("Threshold parameters must be between 1 to 99.");
            return result;
        } else {
            commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "  check threshold between [1,99] -> OK"); // FIXME
        }
        if (warning >= critical) {
            log.error("Critical threshold must be greater than warning one.");
            return false;
        } else {
            commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "  check warning < critical -> OK");
        }
        return true;
    }

    @Override
    public void setup() {
        super.setup();

        String commentUNDO = COMMENT, commentTBS = COMMENT;

        if ("n".equals(undo)) {
            commentUNDO = "";
        }

        String inexclude = StringUtils.replace(exclude.toString(), "[", "(", "]", ")");
        if (inexclude.length() > 2) {
            commentTBS = "";
        }

        this.query = Query.getTablespacesAllocation(inexclude, "'UNDO'", warning, commentTBS, commentUNDO);

        commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "> query to execute:\n" + this.query.replaceAll("(?m)^", "  ") + "\n");
    }

    @Override
    public void runJob(int w) {
        String filename = String.format("%s-%s.txt", databasePoolManager.getSid(), Sysdate.now(Sysdate.SQUELCHED_TIMEDATE));
        String path = String.format("%s/%s", directory, filename);
        Filepath filepath = new DefaultFilepath(path);
        log.info("Output file is:\n   " + filepath.getFilepath());
        jdbc.query(query, (ResultSetExtractor<Integer>) rs -> {
            try {
                return write(rs, filepath);
            } catch (IOException e) {
                log.error("Error while writing to file {}\n -> {}", filepath.getFilepath(), e.getMessage());
            } catch (SQLException e) {
                commandLinePresentation.print(LogLevel.ERROR, "Error while fetching data\n  -> %s", e.getMessage());
            }
            return -1;
        });
        commandLinePresentation.print(LogLevel.DEBUG, "  -> data written to the file %s", path);
    }

    protected abstract int write(final ResultSet rs, Filepath filename) throws SQLException, IOException;

    @Override
    public void takedown() {
        super.takedown();
    }
}
