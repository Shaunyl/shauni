package com.fil.shauni.command.writer;

import com.fil.shauni.util.DatabaseUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NonNull;

/**
 *
 * @author Shaunyl
 */
public class MemWriter extends DefaultWriter {

    public MemWriter(Writer writer) {
        this.rawWriter = writer;
        this.printer = new PrintWriter(writer);
    }
    
    @Override
    public int writeAll(@NonNull final ResultSet rs, boolean includeColumnNames) throws SQLException, IOException {
        return super.writeAll(rs, includeColumnNames);
    }

    @Override
    public void writeAll(@NonNull final List lines) {
    }

    public void flush() throws IOException {
        printer.flush();
    }

    @Override
    public void close() throws IOException {
        printer.flush();
        printer.close();
        rawWriter.close();
    }

    @Override
    public String[] getValidFileExtensions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeHeader() {
        writeNext("This is the HEADER");
    }

    @Override
    public void writeFooter() {
        writeNext("This is the FOOTER");
    }

    @Override
    public void formatLine(String[] record) {
        double used_mb = Double.parseDouble(record[1]);
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        String used = formatter.format(used_mb);
        String buffer = String.format("  %-10d%11s%15s",
                (int) Double.parseDouble(record[0]),
                used,
                record[2]);
        writeNext(new String[]{buffer});
    }
}
