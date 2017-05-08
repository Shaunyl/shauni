package com.fil.shauni.command.montbs;

import com.fil.shauni.command.support.Query;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.CommaParameterSplitter;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.validators.PositiveInteger;
import com.fil.shauni.command.Check;
import com.fil.shauni.command.DatabaseCommandControl;
import com.fil.shauni.command.support.StatementManager;
import com.fil.shauni.command.support.CharBooleanValidator;
import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.log.LogLevel;
import com.fil.shauni.mainframe.ui.CommandLinePresentation;
import com.fil.shauni.util.DateFormat;
import com.fil.shauni.util.file.DefaultFilename;
import com.fil.shauni.util.GeneralUtil;
import com.fil.shauni.util.StringUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Shaunyl
 */
@Log4j2 @NoArgsConstructor
public abstract class DefaultMonTbs extends DatabaseCommandControl {

//    @Parameter(required = true, arity = 1)
//    private final List<String> cmd = Lists.newArrayList(1);

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

    @Inject
    private StatementManager statementManager;

    private final String COMMENT = "--";

    private String query;

    public DefaultMonTbs(String name) {
        this.name = name;
        this.isCluster = true;
    }

    @Override
    public Check validate() throws ShauniException {
        if ((warning | critical) < 1 || (warning | critical) > 99) {
            return new Check(false, 1120, "Threshold parameters must be between 1 to 99.");
//            throw new ShauniException(1020, "Warning: Threshold parameters must be between 1 to 99.");
        } else  {
            commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "  check threshold between [1,99] -> OK"); // FIXME
        }
        if (warning >= critical) {
            return new Check(false, 1121, "Critical threshold must be greater than warning one.");
        } else {
            commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "  check warning < critical -> OK");
        }
        return new Check();
    }

    @Override
    public void setup() throws ShauniException {
        super.setup();

        String commentUNDO = COMMENT;
        String commentTBS = COMMENT;

        if ("n".equals(undo)) {
            commentUNDO = "";
        }

        String inexclude = StringUtil.replace(exclude.toString(), "[", "(", "]", ")"); 
        if (inexclude.length() > 2) {
            commentTBS = "";
        }

        this.query = Query.getTablespacesAllocation(inexclude, "'UNDO'", warning, commentTBS, commentUNDO);
        
        commandLinePresentation.printIf(firstThread, LogLevel.DEBUG, "> query to execute:\n" + this.query.replaceAll("(?m)^", "  ") + "\n");   
    }
    
    @Override
    public void run() throws ShauniException {
        super.run();
//        Connection connection = databasePoolManager.getConnection();
//        if (connection == null) {
//            log.error("> Worker {} could not connect to {}@{}", 1, databasePoolManager.getSid(), databasePoolManager.getHost());
//            return;
//        }
        Connection connection = super.getConnection(1);

        Statement statement = statementManager.createStatement(connection, 20);
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(query);
            log.info("Extracting data..");
        } catch (SQLException e) {
            commandLinePresentation.print(LogLevel.ERROR, "Error while fetching data\n  -> %s", e.getMessage());
            return;
        }
        if (rs == null) {
            commandLinePresentation.print(LogLevel.ERROR, " . . (worker %d) error while fetching datar\n  -> Result Set is null");
            return;
        }
        String filename = String.format("%s-%s.txt", databasePoolManager.getSid(), GeneralUtil.getCurrentDate(DateFormat.SQUELCHED_TIMEDATE.toString()));
        String path = String.format("%s/%s", directory, filename);
        DefaultFilename fn = new DefaultFilename(path, filename);
        log.info("Output file is:\n   " + fn.getPath());
        try {
            write(rs, fn);
        } catch (IOException ex) {
            throw new ShauniException(1005, "Error while writing data to the file " + filename + "\n -> " + ex.getMessage());
        } catch (SQLException ex) {
            throw new ShauniException(1006, "Error while reading the result set\n -> " + ex.getMessage());
        }//modify Filename, just path, can calculate name itself...
        commandLinePresentation.print(LogLevel.DEBUG, "  -> data written to the file %s", path);
    }

    //TEMP: need to be abstract.. because i wanna unbound the export action with the writer one..
    // If I want to combine multiple writing in a single task, I cannot do that if they are coupled..
    // This classe need to be abstract, 
    public abstract int write(final ResultSet rs, DefaultFilename filename) throws SQLException, IOException;

    @Override
    public void takedown() {
        super.takedown();
    }
}
