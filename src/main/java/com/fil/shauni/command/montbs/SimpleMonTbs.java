package com.fil.shauni.command.montbs;

import com.beust.jcommander.Parameters;
import com.fil.shauni.command.writer.spi.MonTbsWriter;
import com.fil.shauni.command.writer.WriterManager;
import com.fil.shauni.util.file.Filepath;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Shaunyl
 */
@Log4j @Component(value = "montbs") @Parameters(separators = "=") @Scope("prototype")
public class SimpleMonTbs extends DefaultMonTbs {

    public SimpleMonTbs() {
        super("montbs");
    }
    
    public SimpleMonTbs(@Qualifier String name) {
        super(name);
    }
    
    @Override
    protected int write(final ResultSet rs, final Filepath filename) throws SQLException, IOException {
        WriterManager writer = new MonTbsWriter(new FileWriter(filename.getFilepath()), databasePoolManager.getSid(), warning, critical, undo, exclude);
        int rows = writer.writeAll(rs, true);
        writer.close();
        return rows;
    }
   
}
