package com.fil.shauni.command.export;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameters;
import com.fil.shauni.command.writer.TabularWriter;
import com.fil.shauni.util.file.Filename;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2 @Component(value = "exptab") @Parameters(separators = "=") @Scope("prototype")
public class TabularExporter extends DefaultExporter {
    
    @Setter @DynamicParameter(names = "-C", description = "Pad the columns")
    private Map<String, Integer> colformats = new HashMap<>();

    public TabularExporter() {
        super("exp");
    }
    
    public TabularExporter(@Qualifier String name) {
        super(name);
    }
    
    @Override
    public int write(ResultSet rs, Filename filename) throws SQLException, IOException {
        TabularWriter writer = new TabularWriter(new FileWriter(filename.getPath() + ".txt"), colformats);
        
        int rows = writer.writeAll(rs, true);
        writer.close();
        log.info("Dump {} created successfully", filename.getName() + ".txt");
        return rows;
    }
}
