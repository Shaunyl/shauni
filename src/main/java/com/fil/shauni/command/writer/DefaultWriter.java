package com.fil.shauni.command.writer;

import com.fil.shauni.util.DatabaseUtil;
import com.fil.shauni.util.StringUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import lombok.NonNull;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;

/**
 *
 * @author Chiara
 */
public abstract class DefaultWriter implements WriterManager {

    protected PrintWriter printer;

    protected String endline, pattern;

    protected char separator;

    public static final String ENDLINE = "\n";

    public static final char SEPARATOR = '-';

    public static final int COLUMN_WIDTH = 25;

    protected final Map<String, Integer> cols = new LinkedHashMap<>();

    public DefaultWriter(@NonNull final Writer rawWriter) {
        this(rawWriter, ENDLINE);
    }

    public DefaultWriter(@NonNull final Writer rawWriter, String endline) {
        this(rawWriter, endline, SEPARATOR);
    }

    public DefaultWriter(@NonNull final Writer rawWriter, String endline, char separator) {
        this.printer = new PrintWriter(rawWriter);
        this.endline = endline;
        this.separator = separator;
    }

    @Override
    public abstract String[] getValidFileExtensions();

    @Override
    public void writeAll(@NonNull List<String[]> lines) throws IOException {
        lines.forEach((nextLine) -> {
            writeNext(nextLine);
        });
    }

    @Override
    public int writeAll(@NonNull ResultSet rs, boolean includeColumnNames) throws SQLException, IOException {
        this.writeHeader();

        int rows = 0;

        if (!rs.next()) {
//            writeNext(new String[]{ "\nno rows selected" });
        } else {
            ResultSetMetaData metadata = rs.getMetaData();

            int cols = metadata.getColumnCount();

            if (includeColumnNames) {
                writeColumnNames(metadata);
            } else {
                pattern = Stream.generate(() -> "%-" + COLUMN_WIDTH + "s").limit(cols).collect(Collectors.joining());
            }
            do {
                String[] nextLine = new String[cols];

                for (int i = 0; i < cols; i++) {
                    nextLine[i] = DatabaseUtil.getColumnValue(rs, metadata.getColumnType(i + 1), i + 1).trim();
                }
                this.formatLine(nextLine);
                rows++;
            } while (rs.next());
        }

        this.writeFooter();
        return rows;
    }

    public abstract void writeHeader();

    public abstract void writeFooter();

    @Override
    public void close() throws IOException {
        printer.close();
    }

    public abstract void formatLine(String[] record);

    protected void writeNext(String[] nextLine) {
        printer.write(String.format(pattern, (Object[]) nextLine) + endline);
    }

    protected void writeNext(String nextLine) {
        printer.write(nextLine + endline);
    }

    private void writeColumnNames(@NonNull final ResultSetMetaData metadata) throws SQLException {
        int columnCount = metadata.getColumnCount();

        String[] nextLine = new String[columnCount];
        String[] separators = new String[columnCount];

        for (int i = 0; i < columnCount; i++) {
            this.buildColumnNames(i, metadata, nextLine, separators);
        }

        this.pattern = buildRowPattern(cols);

        writeNext(nextLine);
        writeNext(separators);
    }

    protected String buildRowPattern(Map<String, Integer> sampleNextLine) {
        return sampleNextLine.entrySet().stream().map(m -> "%-" + m.getValue() + "s").collect(joining(" "));
    }

    protected void buildColumnNames(int i, ResultSetMetaData metadata, String[] nextLine, String[] separators) throws SQLException {
        nextLine[i] = metadata.getColumnName(i + 1).toLowerCase();
        cols.put(nextLine[i], COLUMN_WIDTH);
        separators[i] = StringUtils.repeat(String.valueOf(separator), COLUMN_WIDTH);
    }
}
