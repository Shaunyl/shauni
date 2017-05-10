package com.fil.shauni.command.writer;

import static com.fil.shauni.command.writer.TabularWriter.DEFAULT_COLUMN_LENGTH;
import com.fil.shauni.util.DatabaseUtil;
import com.fil.shauni.util.DateFormat;
import com.fil.shauni.util.GeneralUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import lombok.NonNull;

/**
 *
 * @author Chiara
 */
public abstract class DefaultWriter implements WriterManager {

    protected Writer rawWriter;

    protected PrintWriter printer;

    private String endline;

    public static final String DEFAULT_END_LINE = "\n";

    public static final char DEFAULT_SEPARATOR = '-';

    @Override
    public abstract String[] getValidFileExtensions();

    @Override
    public void writeAll(List allLines) throws IOException {
    }

    @Override
    public int writeAll(ResultSet rs, boolean includeColumnNames) throws SQLException, IOException {
        writeHeader();
        int rows = 0;
        try {
            ResultSetMetaData metadata = rs.getMetaData();
            if (includeColumnNames) {
                writeColumnNames(metadata);
            }
            int columnCount = metadata.getColumnCount();

            while (rs.next()) {
                String[] nextLine = new String[columnCount];

                for (int i = 0; i < columnCount; i++) {
                    nextLine[i] = DatabaseUtil.getColumnValue(rs, metadata.getColumnType(i + 1), i + 1).trim();
                }
                formatLine(nextLine);
                rows++;
            }
        } catch (SQLException | IOException ex) {
            throw new SQLException(ex.getMessage(), ex);
        } finally {
            writeFooter();
        }

        return rows;
    }

    public abstract void writeHeader();

    public abstract void writeFooter();

    @Override
    public void close() throws IOException {
        printer.flush();
        printer.close();
        rawWriter.close();
    }

    public abstract void formatLine(String[] record);

    protected void writeNext(String[] nextLine) {
        String pattern = "";
        StringBuilder sb = new StringBuilder();

        int len = DEFAULT_COLUMN_LENGTH;

        for (int i = 0; i < nextLine.length; i++) {
            pattern += "%-" + len + "s ";
        }
        sb.append(String.format(pattern, (Object[]) nextLine));

        sb.append(DEFAULT_END_LINE);
        printer.write(sb.toString());
    }

    protected void writeNext(String nextLine) {
        printer.write(nextLine + "\n");
    }

    private void writeColumnNames(@NonNull final ResultSetMetaData metadata)
            throws SQLException {

        int columnCount = metadata.getColumnCount();

        String[] nextLine = new String[columnCount];
        String[] separators = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            nextLine[i] = metadata.getColumnName(i + 1);
        }

        for (int i = 0; i < columnCount; i++) {
            separators[i] = GeneralUtil.repeat(String.valueOf(DEFAULT_SEPARATOR), DEFAULT_COLUMN_LENGTH);
        }
        writeNext(nextLine);
        writeNext(separators);
    }
}
