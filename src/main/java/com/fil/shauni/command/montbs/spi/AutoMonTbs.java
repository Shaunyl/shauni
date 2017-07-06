package com.fil.shauni.command.montbs.spi;

import com.beust.jcommander.Parameters;
import com.fil.shauni.command.montbs.DefaultMonTbs;
import com.fil.shauni.command.writer.spi.montbs.config.MontbsWriterConfiguration;
import com.fil.shauni.command.writer.WriterConfiguration;
import com.fil.shauni.command.writer.spi.montbs.DefaultMonTbsWriter;
import com.fil.shauni.util.file.Filepath;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Filippo
 */
@Log4j2 @Component(value = "autotbs") @Parameters(separators = "=") @Scope("prototype")
public class AutoMonTbs extends DefaultMonTbs {

    @Override
    protected int write(final ResultSet rs, final Filepath filename) throws SQLException, IOException {
        int rows;
        WriterConfiguration config = new MontbsWriterConfiguration.Builder(hostname, dbname)
                .writer(new FileWriter(filename.getFilepath()))
                .critical(critical).warning(warning).growing(growing)
                .unit(unit).exclude(exclude).undo(undo)
                .auto(autoextend)
                .build();

        try (DefaultMonTbsWriter writer = new DefaultMonTbsWriter(config)) {
            rows = writer.writeAll(rs, true);
        }
        
        return rows;
    }
   
}

