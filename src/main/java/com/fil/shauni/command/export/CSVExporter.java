package com.fil.shauni.command.export;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.validators.PositiveInteger;
import com.fil.shauni.command.export.support.WildcardReplacer;
import com.fil.shauni.command.writer.CSVWriter;
import com.fil.shauni.command.writer.WriterManager;
import com.fil.shauni.util.file.Filename;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Shaunyl
 */
@Log4j @Component(value = "expcsv") @Parameters(separators = "=") @Scope("prototype")
public class CSVExporter extends DefaultExporter {
    
    @Parameter(names = "-start", arity = 1, validateWith = PositiveInteger.class, description = "Line at which starting to export")
    private Integer start = -1;

    @Parameter(names = "-end", arity = 1, validateWith = PositiveInteger.class, description = "Line up to which ending to export")
    private Integer end = -1;

    @Parameter(names = "-delimiter", arity = 1, description = "CSV deliminer")
    private final String delimiter = ",";
    
    public CSVExporter() {
        super("exp");
    }
    
    public CSVExporter(@Qualifier String name) {
        super(name);
    }
    
    public CSVExporter(@Qualifier String name, @Qualifier Set<WildcardReplacer> replacers) {
        super(name, replacers);
    }
    
    @Override
    public int write(ResultSet rs, Filename filename) throws SQLException, IOException {
        WriterManager writer = new CSVWriter(new FileWriter(filename.getPath() + ".csv"), delimiter, start, end);
        int rows = writer.writeAll(rs, true);
        writer.close();
        log.debug("Dump " + filename.getName() + " created successfully");
        return rows;
    }
}
