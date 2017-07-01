package com.fil.shauni.command.montbs.spi;

import com.beust.jcommander.Parameters;
import com.fil.shauni.command.montbs.DefaultMonTbs;
import com.fil.shauni.command.writer.spi.montbs.DefaultMonTbsWriter;
import com.fil.shauni.db.spring.service.MontbsRunService;
import com.fil.shauni.util.file.Filepath;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@Log4j2 @Component(value = "montbs") @Parameters(separators = "=") @Scope("prototype")
public class SimpleMonTbs extends DefaultMonTbs {
    
    @Autowired
    private MontbsRunService service;
    
    @Override
    protected int write(final ResultSet rs, final Filepath filename) throws SQLException, IOException {
        int rows;
        try (DefaultMonTbsWriter writer = new DefaultMonTbsWriter(
                new FileWriter(filename.getFilepath()), databasePoolManager.getHost(), databasePoolManager.getSid()
                , warning, critical, undo, unit, exclude, growing)) {
            writer.setService(service);
            rows = writer.writeAll(rs, true);
        }
        return rows;
    }
   
}
