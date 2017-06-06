//package com.fil.shauni.command.memory;
//
//import com.beust.jcommander.Parameter;
//import com.beust.jcommander.Parameters;
//import com.fil.shauni.command.Check;
//import com.fil.shauni.command.DatabaseCommandControl;
//import com.fil.shauni.command.support.Query;
//import com.fil.shauni.command.support.StatementManager;
//import com.fil.shauni.command.writer.MemWriter;
//import com.fil.shauni.command.writer.WriterManager;
//import com.fil.shauni.exception.ShauniException;
//import com.fil.shauni.log.LogLevel;
//import com.fil.shauni.util.file.DefaultFilepath;
//import com.fil.shauni.util.Sysdate;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import javax.inject.Inject;
//import lombok.NoArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.stereotype.Component;
//
///**
// *
// * @author Filippo Testino (filippo.testino@gmail.com)
// */
//@Log4j2 @Component(value = "monmem") @Parameters(separators = "=") @NoArgsConstructor
//public class DefaultMonMem extends DatabaseCommandControl {
//    
//    @Parameter(names = "-directory", arity = 1)
//    protected String directory = ".";
//
//    @Inject
//    private StatementManager statementManager;
//    
//    public DefaultMonMem(String name) {
//        this.name = name;
//    }
//
//    @Override
//    public Check validate() throws ShauniException {
//        cli.printIf(firstThread, LogLevel.DEBUG, "No parameters to validate.");
//        return new Check();
//    }
//  
//    @Override
//    public void runThread() {
//        String query = Query.getTotalPGAUsed();
//        Connection connection = databasePoolManager.getConnection();
//        Statement statement = statementManager.createStatement(connection, 20);
//        ResultSet rs = null;
//        try {
//            rs = statement.executeQuery(query);
//        } catch (SQLException e) {
//            cli.print(LogLevel.ERROR, "Error while fetching data\n  -> %s", e.getMessage());
//        }
//        if (rs == null) {
//            cli.print(LogLevel.ERROR, " . . (worker %d) error while fetching datar\n  -> Result Set is null");
//        }
//        String filename = String.format("%s-%s.txt", databasePoolManager.getSid(), Sysdate.now(Sysdate.DASH_TIMEDATE));
//        String path = String.format("%s/%s", directory, filename);
//        DefaultFilepath fn = new DefaultFilepath(path);
//        try {
//            write(rs, fn);
//        } catch (IOException ex) {
//            log.error("Error while writing data to the file " + filename + "\n -> " + ex.getMessage());
//        } catch (SQLException ex) {
//            log.error("Error while reading the result set\n -> " + ex.getMessage());
//        } //modify Filename, just path, can calculate name itself...
//        cli.print(LogLevel.DEBUG, "  -> data written to the file %s", path);
//    }
//    
//    public int write(final ResultSet rs, final DefaultFilepath filename) throws SQLException, IOException {
//        WriterManager writer = new MemWriter(new FileWriter(filename.getFilename()));
//        int rows = writer.writeAll(rs, true);
//        writer.close();
//        return rows;
//    }
//}