package com.fil.shauni.command.export;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.internal.Lists;
import com.fil.shauni.command.writer.TabularWriter;
import com.fil.shauni.util.file.Filename;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j @Component(value = "exptab") @Parameters(separators = "=") @Scope("prototype")
public class TabularExporter extends DefaultExporter {

    @Parameter(required = true, arity = 1)
    private final List<String> cmd = Lists.newArrayList(1);
    
    @Setter @DynamicParameter(names = "-C", description = "Pad the columns")
    private Map<String, Integer> colformats = new HashMap<>();
    
    @Setter
    private boolean includeColumnNames = false;

    public TabularExporter() {
        super("exp");
    }
    
    public TabularExporter(@Qualifier String name) {
        super(name);
    }
    
    @Override
    public int write(ResultSet rs, Filename filename) throws SQLException, IOException {
        TabularWriter writer = new TabularWriter(new FileWriter(filename.getPath() + ".txt"), colformats);
        
        int rows = writer.writeAll(rs, includeColumnNames);
        writer.close();
        log.debug("Dump " + filename.getName() + " created successfully");
        return rows;
    }
}
