package com.fil.shauni.command.montbs.spi;

import com.beust.jcommander.Parameters;
import com.fil.shauni.command.montbs.DefaultMonTbs;
import com.fil.shauni.command.writer.WriterManager;
import com.fil.shauni.command.writer.spi.montbs.AutoMonTbsWriter;
import com.fil.shauni.util.file.Filepath;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.log4j.Log4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Filippo
 */
@Log4j @Component(value = "autotbs") @Parameters(separators = "=") @Scope("prototype")
public class AutoMonTbs extends DefaultMonTbs {

    @Override
    protected int write(final ResultSet rs, final Filepath filename) throws SQLException, IOException {
        int rows;
        try (WriterManager writer = new AutoMonTbsWriter (
                new FileWriter(filename.getFilepath()), databasePoolManager.getSid()
                , warning, critical, undo, unit, exclude)) {
            rows = writer.writeAll(rs, true);
        }
        return rows;
    }
   
}

