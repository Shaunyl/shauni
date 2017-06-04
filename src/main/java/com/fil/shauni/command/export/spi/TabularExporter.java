package com.fil.shauni.command.export.spi;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameters;
import com.fil.shauni.command.export.SpringExporter;
import com.fil.shauni.command.support.worksplitter.WorkSplitter;
import com.fil.shauni.command.writer.spi.TabularWriter;
import com.fil.shauni.mainframe.spi.CommandConfiguration;
import com.fil.shauni.util.Processor;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import com.fil.shauni.util.file.Filepath;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2 
@Component(value = "exptab") 
@Parameters(separators = "=") 
@Scope("prototype")
public class TabularExporter extends SpringExporter {

    @Setter @DynamicParameter(names = "-C", description = "Pad the columns")
    private Map<String, Integer> colformats = new HashMap<>();

    private final static String EXTENSION = ".txt";

    @Inject
    public TabularExporter(@Value("#{wildcardReplacers}") List<Processor<Filepath, WildcardContext>> replacers
            , @Value("#{workSplitter}") WorkSplitter<String> workSplitter) {
        super(replacers, workSplitter);
    }
    
    @Override
    public int write(ResultSet rs, Filepath filename) throws SQLException, IOException {
        String file = filename.getFilepath() + EXTENSION; // FIXME.. should be global not local....
        int rows;
        try (TabularWriter writer = new TabularWriter(new FileWriter(file), colformats)) {
            rows = writer.writeAll(rs, true);
        }
        if (rows == 0) {
            Files.deleteIfExists(Paths.get(filename.getAbsoluteFilePath() + EXTENSION));
        } else {
            log.info("Dump {} created successfully.", file);
        }
        return rows;
    }
}
