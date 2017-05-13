package com.fil.shauni.command.writer;

import com.fil.shauni.util.GeneralUtil;
import com.fil.shauni.util.DatabaseUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 *
 * @author Shaunyl
 */
@NoArgsConstructor
public class TabularWriterOld implements WriterManager {

    private Writer rawWriter;

    private PrintWriter printer;

    private char separator;

    private String endline;

    private Map<String, Integer> colformats;

    private final Map<String, Integer> cols = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /**
     * The default separator to use if none is supplied to the constructor.
     */
    public static final char DEFAULT_SEPARATOR = '-';

    /**
     * Default line terminator uses platform encoding.
     */
    public static final String DEFAULT_LINE_END = "\n";

    private static final int DEFAULT_COLUMN_LENGTH = 25;

    /**
     * Constructs TabularWriter using a dash for the delimiter.
     *
     * @param writer the writer to an underlying Tabular source.
     */
    public TabularWriterOld(Writer writer) {
        this(writer, null);
    }

    /**
     * Constructs TabularWriter using a dash for the delimiter.
     *
     * @param writer the writer to an underlying Tabular source.
     * @param colformats formats specified for columns.
     */
    public TabularWriterOld(Writer writer, Map<String, Integer> colformats) {
        this(writer, colformats, DEFAULT_SEPARATOR);
    }

    /**
     * Constructs TabularWriter with supplied separator.
     *
     * @param writer the writer to an underlying Tabular source.
     * @param colformats
     * @param separator the separator to use for separating header from data.
     */
    public TabularWriterOld(Writer writer, Map<String, Integer> colformats, char separator) {
        this(writer, colformats, separator, DEFAULT_LINE_END);
    }

    /**
     * Constructs TabularWriter with supplied separator and line ending.
     *
     * @param writer the writer to an underlying Tabular source.
     * @param colformats
     * @param separator the separator to use for separating header from data.
     * @param endline the line feed terminator to use.
     */
    public TabularWriterOld(Writer writer, Map<String, Integer> colformats, char separator, String endline) {
        this.rawWriter = writer;
        this.printer = new PrintWriter(writer, false);
        this.separator = separator;
        this.endline = endline;
        this.colformats = colformats == null ? new TreeMap<>(String.CASE_INSENSITIVE_ORDER) : colformats;
    }

    private void writeColumnNames(@NonNull final ResultSetMetaData metadata)
            throws SQLException {

        int columnCount = metadata.getColumnCount();

        final String[] nextLine = new String[columnCount];
        String[] separators = new String[columnCount];

        for (int i = 0; i < columnCount; i++) {
            nextLine[i] = metadata.getColumnName(i + 1);
            int width = DEFAULT_COLUMN_LENGTH;
            if (colformats.containsKey(nextLine[i])) {
                width = colformats.get(nextLine[i]);
            }
            cols.put(nextLine[i], width);

            separators[i] = GeneralUtil.repeat(String.valueOf(separator), width);
        }

        writeNext(nextLine);
        writeNext(separators);
    }

    /**
     * Writes the entire ResultSet to a Tabular file.
     *
     * The caller is responsible for closing the ResultSet.
     *
     * @param rs the recordset to write (cannot be null)
     * @param includeColumnNames true if you want column names in the output,
     * false otherwise
     * @return
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     *
     */
    @Override
    public int writeAll(@NonNull final ResultSet rs, boolean includeColumnNames)
            throws SQLException, IOException {
        ResultSetMetaData metadata = rs.getMetaData();

        if (includeColumnNames) {
            writeColumnNames(metadata);
        }

        int columnCount = metadata.getColumnCount();

        boolean norows = true;
        int rows = 0;
        while (rs.next()) {
            norows = false;
            String[] nextLine = new String[columnCount];

            for (int i = 0; i < columnCount; i++) {
                nextLine[i] = DatabaseUtil.getColumnValue(rs, metadata.getColumnType(i + 1), i + 1);
            }

            writeNext(nextLine);
            rows++;
        }
        if (norows) {
            writeNext(new String[]{ "\nno rows selected" });
        }
        return rows;
    }

    /**
     * Gets a list of supported extensions.
     *
     * @return
     */
    @Override
    public String[] getValidFileExtensions() {
        return new String[]{ ".txt" };
    }

    /**
     * Writes the entire list to a Tabular file. The list is assumed to be a
     * String[].
     *
     * @param lines a List of String[], with each String[] representing a line
     * of the file.
     * @throws java.io.IOException
     */
    @Override
    public void writeAll(@NonNull final List lines) throws IOException {
        for (Iterator iter = lines.iterator(); iter.hasNext();) {
            String[] nextLine = (String[]) iter.next();
            writeNext(nextLine);
        }
    }

    /**
     * Writes the next line to the file.
     *
     * @param nextLine a string array with each element as a separate entry.
     */
    public void writeNext(String[] nextLine) {
        String pattern = cols.entrySet().stream().map(m -> "%-" + m.getValue() + "s").collect(Collectors.joining(" "));
        printer.write(String.format(pattern + endline, (Object[]) nextLine));
    }

    /**
     * Flush underlying stream to writer.
     *
     * @throws IOException if bad things happen
     */
    public void flush() throws IOException {
        printer.flush();
    }

    /**
     * Close the underlying stream writer flushing any buffered content.
     *
     * @throws IOException if bad things happen
     *
     */
    @Override
    public void close() throws IOException {
        flush();
        printer.close();
        rawWriter.close();
    }
}