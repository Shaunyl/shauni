package com.fil.shauni.command.memory;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.fil.shauni.command.DatabaseCommandControl;
import com.fil.shauni.command.support.Query;
import com.fil.shauni.command.support.StatementManager;
import com.fil.shauni.command.writer.MemWriter;
import com.fil.shauni.command.writer.WriterManager;
import com.fil.shauni.log.LogLevel;
import com.fil.shauni.mainframe.ui.CommandLinePresentation;
import com.fil.shauni.util.DateFormat;
import com.fil.shauni.util.file.DefaultFilename;
import com.fil.shauni.util.GeneralUtil;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author Shaunyl
 */
@Log4j @Component(value = "monmem") @Parameters(separators = "=") @NoArgsConstructor
public class DefaultMonMem extends DatabaseCommandControl {
//    @Parameter(required = true, arity = 1)
//    private final List<String> cmd = Lists.newArrayList(1);
    
    @Parameter(names = "-directory", arity = 1)
    protected String directory = ".";
    
    @Inject
    private CommandLinePresentation commandLinePresentation;

    @Inject
    private StatementManager statementManager;
    
    public DefaultMonMem(String name) {
        this.name = name;
    }
    
    @Override
    public void run() {
        String query = Query.getTotalPGAUsed();
        Connection connection = databasePoolManager.getConnection();
        Statement statement = statementManager.createStatement(connection, 20);
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            commandLinePresentation.print(LogLevel.ERROR, "Error while fetching data\n  -> %s", e.getMessage());
        }
        if (rs == null) {
            commandLinePresentation.print(LogLevel.ERROR, " . . (worker %d) error while fetching datar\n  -> Result Set is null");
        }
        String filename = String.format("%s-%s.txt", databasePoolManager.getSid(), GeneralUtil.getCurrentDate(DateFormat.SQUELCHED_TIMEDATE.toString()));
        String path = String.format("%s/%s", directory, filename);
        DefaultFilename fn = new DefaultFilename(path, filename);
        try {
            write(rs, fn);
        } catch (IOException ex) {
            log.error("Error while writing data to the file " + filename + "\n -> " + ex.getMessage());
        } catch (SQLException ex) {
            log.error("Error while reading the result set\n -> " + ex.getMessage());
        } //modify Filename, just path, can calculate name itself...
        commandLinePresentation.print(LogLevel.DEBUG, "  -> data written to the file %s", path);
    }
    
    public int write(final ResultSet rs, final DefaultFilename filename) throws SQLException, IOException {
        WriterManager writer = new MemWriter(new FileWriter(filename.getPath()));
        int rows = writer.writeAll(rs, true);
        writer.close();
        return rows;
    }
}