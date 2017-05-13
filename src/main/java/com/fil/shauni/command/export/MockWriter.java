package com.fil.shauni.command.export;

import com.fil.shauni.command.writer.TabularWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import lombok.NonNull;

/**
 *
 * @author Chiara
 */
public class MockWriter extends TabularWriter {

    public MockWriter(Writer writer) {
        super(writer);
    }
    
    public MockWriter(Writer writer, Map<String, Integer> colformats) {
        super(writer, colformats);
    }
    
    @Override
    public int writeAll(@NonNull final ResultSet rs, boolean includeColumnNames)
            throws SQLException, IOException {
        int columnCount = 3;

        boolean norows = true;
        int rows = 0;
        while (rs.next()) {
            norows = false;
            String[] nextLine = new String[columnCount];

            for (int i = 0; i < columnCount; i++) {
                String value = rs.getString(i + 1);
                if (value == null) {
                    value = String.valueOf(rs.getInt(i + 1));
                }
                nextLine[i] = value;
            }

            writeNext(nextLine);
            rows++;
        }
        if (norows) {
            writeNext(new String[]{"\nno rows selected"});
        }
        super.close();
        return rows;
    }
}
