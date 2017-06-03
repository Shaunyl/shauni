package com.fil.shauni.command.export.spi;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.validators.PositiveInteger;
import com.fil.shauni.command.export.SpringExporter;
import com.fil.shauni.command.support.worksplitter.WorkSplitter;
import com.fil.shauni.command.writer.spi.CSVWriter;
import com.fil.shauni.command.writer.WriterManager;
import com.fil.shauni.util.Processor;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import com.fil.shauni.util.file.Filepath;
import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Shaunyl
 */
@Log4j2 
@Component(value = "expcsv")
@Parameters(separators = "=")
@Scope("prototype")
public class CSVExporter extends SpringExporter {

    @Parameter(names = "-start", arity = 1, validateWith = PositiveInteger.class, description = "Line at which starting to export")
    private Integer start = -1;

    @Parameter(names = "-end", arity = 1, validateWith = PositiveInteger.class, description = "Line up to which ending to export")
    private Integer end = -1;

    @Parameter(names = "-delimiter", arity = 1, description = "CSV deliminer")
    private final String delimiter = ",";

    private final static String EXTENSION = ".txt";
    
//    @Inject
//    public CSVExporter(@Value("#{wildcardReplacers}") List<Processor<Filepath, WildcardContext>> replacers, WorkSplitter<String> workSplitter) {
//        super(replacers, workSplitter);
//    }
//    

    public CSVExporter(List<Processor<Filepath, WildcardContext>> replacers, WorkSplitter<String> workSplitter) {
        super(replacers, workSplitter);
    }

    @Override
    public int write(ResultSet rs, Filepath filename) throws SQLException, IOException {
        String file = filename.getFilepath() + EXTENSION;
        @Cleanup
        WriterManager writer = new CSVWriter(new FileWriter(file), delimiter, start, end);
        int rows = writer.writeAll(rs, true);
        log.info("Dump {} created successfully", file);
        return rows;
    }
}