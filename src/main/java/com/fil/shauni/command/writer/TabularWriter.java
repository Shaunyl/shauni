package com.fil.shauni.command.writer;

import com.fil.shauni.util.GeneralUtil;
import java.io.Writer;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * @author Shaunyl
 */
public class TabularWriter extends DefaultWriter {

    private Map<String, Integer> colformats;

    /**
     * Constructs TabularWriter using a dash for the delimiter.
     *
     * @param writer the writer to an underlying Tabular source.
     */
    public TabularWriter(Writer writer) {
        this(writer, null);
    }

    /**
     * Constructs TabularWriter using a dash for the delimiter.
     *
     * @param writer the writer to an underlying Tabular source.
     * @param colformats formats specified for columns.
     */
    public TabularWriter(Writer writer, Map<String, Integer> colformats) {
        this(writer, DEFAULT_SEPARATOR, colformats);
    }

    /**
     * Constructs TabularWriter with supplied separator.
     *
     * @param writer the writer to an underlying Tabular source.
     * @param colformats
     * @param separator the separator to use for separating header from data.
     */
    public TabularWriter(Writer writer, char separator, Map<String, Integer> colformats) {
        this(writer, separator, DEFAULT_END_LINE, colformats);
    }

    /**
     * Constructs TabularWriter with supplied separator and line ending.
     *
     * @param writer the writer to an underlying Tabular source.
     * @param colformats
     * @param separator the separator to use for separating header from data.
     * @param endline the line feed terminator to use.
     */
    public TabularWriter(Writer writer, char separator, String endline, Map<String, Integer> colformats) {
        super(writer, endline, separator);
        this.colformats = colformats == null ? new TreeMap<>(String.CASE_INSENSITIVE_ORDER) : colformats;
    }

    @Override
    protected void buildColumnNames(int i, ResultSetMetaData metadata, String[] nextLine, String[] separators) throws SQLException {
        nextLine[i] = metadata.getColumnName(i + 1);
        int width = DEFAULT_COLUMN_LENGTH;
        if (colformats.containsKey(nextLine[i])) {
            width = colformats.get(nextLine[i]);
        }
        cols.put(nextLine[i], width);

        separators[i] = GeneralUtil.repeat(String.valueOf(separator), width);
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
     * Writes the next line to the file.
     *
     * @param nextLine a string array with each element as a separate entry.
     */
    @Override
    public void writeNext(String[] nextLine) {
        printer.write(String.format(pattern + endline, (Object[]) nextLine));
    }

    @Override
    public void writeHeader() {
    }

    @Override
    public void writeFooter() {
    }

    @Override
    public void formatLine(String[] record) {
        writeNext(record);
    }

    @Override
    protected String buildRowPattern(Map<String, Integer> sampleNextLine) { // Dont like this..
        return sampleNextLine.entrySet().stream().map(m -> "%-" + m.getValue() + "s").collect(Collectors.joining(" "));
    }

}
