package com.fil.shauni.command.writer;

import com.fil.shauni.db.spring.service.ShauniService;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
public interface WriterManager extends AutoCloseable {

    public String[] getValidFileExtensions();

    public void writeAll(final List<String[]> allLines) throws IOException;

    public int writeAll(final ResultSet rs, final boolean includeColumnNames)
            throws SQLException, IOException;
    
    @Override
    public void close() throws IOException;
}
