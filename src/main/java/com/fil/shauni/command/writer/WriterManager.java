package com.fil.shauni.command.writer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Shaunyl
 */
public interface WriterManager {

    public String[] getValidFileExtensions();

    public void writeAll(final List allLines) throws IOException;

    public int writeAll(final ResultSet rs, final boolean includeColumnNames)
            throws SQLException, IOException;
    
    public void close() throws IOException;
}
