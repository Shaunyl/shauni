package com.fil.shauni.command.writer.spi;

import com.fil.shauni.command.writer.DefaultWriter;
import com.fil.shauni.util.StringUtils;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.*;
import lombok.NonNull;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
public class MemWriter extends DefaultWriter {

    private final Integer[] widths = new Integer[]{ 7, 20, 20 };

    public MemWriter(Writer writer) {
        super(writer);
    }

    @Override
    public int writeAll(@NonNull final ResultSet rs, boolean includeColumnNames) throws SQLException, IOException {
        return super.writeAll(rs, includeColumnNames);
    }

    @Override
    public void writeAll(@NonNull final List lines) {
    }

    @Override
    public String[] getValidFileExtensions() {
        return new String[]{ ".txt" };
    }

    @Override
    public void writeHeader() {
        writeNext("Retrieving information of memory..");
    }

    @Override
    public void writeFooter() {
        writeNext("Report terminated");
    }

    @Override
    public void formatLine(String[] record) {
        double used_mb = Double.parseDouble(record[1]);
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        String used = formatter.format(used_mb);
        writeNext(new String[]{ String.valueOf((int)Double.parseDouble(record[0])), used, record[2] });
    }

    @Override
    protected void buildColumnNames(int i, ResultSetMetaData metadata, String[] nextLine, String[] separators) throws SQLException {
        nextLine[i] = metadata.getColumnName(i + 1);
        cols.put(nextLine[i], widths[i]);
        separators[i] = StringUtils.repeat(String.valueOf(separator), widths[i]);
    }
    
    @Override
    public void writeNext(String[] nextLine) {
        printer.write(String.format(pattern + endline, (Object[]) nextLine));
    }

    @Override
    protected String buildRowPattern(Map<String, Integer> sampleNextLine) {
        return sampleNextLine.entrySet().stream().map(m -> "%-" + m.getValue() + "s").collect(joining(" "));
    }

}
