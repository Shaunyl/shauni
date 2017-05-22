package com.fil.shauni.command.export;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameters;
import com.fil.shauni.command.export.support.WildcardReplacer;
import com.fil.shauni.command.support.WorkSplitter;
import com.fil.shauni.command.writer.TabularWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Cleanup;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.fil.shauni.util.file.Filepath;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;

@Log4j2 @Component(value = "exptab") @Parameters(separators = "=") @Scope("prototype")
public class TabularExporter extends SpringExporter {

    @Setter @DynamicParameter(names = "-C", description = "Pad the columns")
    private Map<String, Integer> colformats = new HashMap<>();

    private final static String EXTENSION = ".txt";
    
    @Inject
    public TabularExporter(@Value("#{wildcardReplacers}") Set<WildcardReplacer> replacers, WorkSplitter<ExporterObject> workSplitter) {
        super(replacers, workSplitter);
    }

    @Override
    public int write(ResultSet rs, Filepath filename) throws SQLException, IOException {
        String file = filename.getFilepath() + EXTENSION;
        @Cleanup
        TabularWriter writer = new TabularWriter(new FileWriter(file), colformats);
        int rows = writer.writeAll(rs, true);
        log.info("Dump {} created successfully.", file);
        return rows;
    }
}
