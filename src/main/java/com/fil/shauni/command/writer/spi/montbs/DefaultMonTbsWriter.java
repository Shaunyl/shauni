package com.fil.shauni.command.writer.spi.montbs;

import com.fil.shauni.command.writer.WriterManager;
import com.fil.shauni.db.spring.model.MontbsRun;
import com.fil.shauni.db.spring.service.MontbsRunService;
import com.fil.shauni.db.spring.service.ShauniService;
import com.fil.shauni.util.DatabaseUtil;
import com.fil.shauni.util.GeneralUtil;
import com.fil.shauni.util.Sysdate;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

/**
 *
 * @author Filippo
 */
public class DefaultMonTbsWriter implements WriterManager {

    private MontbsRunService service;

    private Writer rawWriter;

    private PrintWriter printer;

    protected int cthreshold, wthreshold;

    private static final boolean UNDO = false;

    protected boolean undo;

    protected String host, instance;

    private static final String INSTANCE = "<n.p.>", HOST = "<n.p.>";

    protected List<String> exclude = null;

    private boolean isCritical, isWarning = false, growing = false;

    protected char unit;

    private static final char UNIT = 'h';

    private final DecimalFormat formatter = new DecimalFormat("#,###.00");

    @Deprecated
    public DefaultMonTbsWriter(Writer writer, int wthreshold, int cthreshold) {
        this(writer, HOST, INSTANCE, wthreshold, cthreshold, UNDO, UNIT, new ArrayList<String>(), false);
    }

    public DefaultMonTbsWriter(Writer writer, String host, String instance, int wthreshold, int cthreshold,
            boolean undo, char unit, List<String> exclude, boolean growing) {
        this.host = host;
        this.instance = instance;
        this.wthreshold = wthreshold;
        this.cthreshold = cthreshold;
        this.undo = undo;
        this.exclude = exclude;
        this.unit = unit;
        this.growing = growing;
        this.rawWriter = writer;
        this.printer = new PrintWriter(writer);
    }

    public void writeHeader() {
        writeNext("Tablespace Report\n");
        writeNext("Starting MonTbs on instance " + instance + " at " + Sysdate.now(Sysdate.DASH_TIMEDATE));
        if (!undo) {
            writeNext("Ignoring tablespace contents -> UNDO");
        }
        if (!exclude.isEmpty()) {
            writeNext("Other tablespaces to ignore -> '" + String.join("', '", exclude) + "'");
        }
        writeNext("Thresholds used -> " + wthreshold + " (warning), " + cthreshold + " (critical)");
        writeNext("\nRetrieving tablespaces info..\n");
    }

    public void writeNext(String nextLine) {
        printer.write(nextLine + "\n");
    }

    public void writeNext(String[] nextLine) {
        StringBuilder sb = new StringBuilder();
        sb.append(nextLine[0]);
        sb.append("\n");
        printer.write(sb.toString());
    }

    @Override
    public int writeAll(@NonNull ResultSet rs, boolean includeColumnNames) throws SQLException, IOException {
        this.writeHeader();
        int rows = 0;
        if (!rs.next()) {
        } else {
            ResultSetMetaData metadata = rs.getMetaData();
            int cols = metadata.getColumnCount();
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

    protected void writeFooter() {
        writeNext("\nJob completed successfully at " + Sysdate.now(Sysdate.DASH_TIMEDATE));
    }

    protected float getPct(String[] record) {
        return Float.parseFloat(record[5]);
    }

    protected void formatLine(String[] record) {
        float csize = getPct(record);

        if (csize > cthreshold) {
            if (!isCritical) {
                writeNext("Check for criticals:");
            }
            isCritical = true;
            retrieveTbsInfo(record);
            return;
        }

        if (csize > wthreshold) {
            if (!isWarning) {
                writeNext("Check for warnings:");
            }
            isWarning = true;
            retrieveTbsInfo(record);
        }
//        else { FIXME: verbose
//            writeNext(" All datafiles of " + record[1] + " are under threshold!");
//        }
    }

    protected void retrieveTbsInfo(String[] record) {
        String pattern = "  %-10s%-38s%11.2f%%  %15s";

        long size_b = (long) Double.parseDouble(record[2]);
        long free_b = (long) Double.parseDouble(record[4]);

        String size = convertToUnit(size_b);
        String free = convertToUnit(free_b);
        float pct = Float.valueOf(record[5]);

        // Bad performance.........!!!!!!!!!! FIXME
        if (growing) {
            MontbsRun lastRun = getLastRun(host, instance, record[1]);
            if (lastRun != null && lastRun.getTotalUsedPercentage() < pct) {
                pattern += "  **" + Double.valueOf(new DecimalFormat("#.##").format(pct - lastRun.getTotalUsedPercentage()));
            }
        }

        String buffer = String.format(pattern,
                instance,
                record[1] + "[" + size + "]",
                pct,
                "(" + free + ")");
        writeNext(new String[]{ buffer });
    }

    private MontbsRun getLastRun(String host, String db, String tbs) {
        if (this.service != null) {
            List<MontbsRun> record = this.service.findAllByHostDbTbsOrderBySampleTimeDesc(host, db, tbs);
            if (record.isEmpty()) {
                return null;
            }
            return record.stream().findFirst().orElse(null);
        }
        return null;
    }

    protected String convertToUnit(long bytes) {
        switch (unit) {
            case 'h':
                return GeneralUtil.byteToHuman(bytes);
            case 'm':
                return formatter.format(bytes / 1048576);
            default:
                return Long.toString(bytes);
        }
    }

    @Override
    public String[] getValidFileExtensions() {
        return new String[]{ ".txt" };
    }

    @Override
    public void writeAll(List<String[]> allLines) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() throws IOException {
        printer.flush();
        printer.close();
        rawWriter.close();
    }

    public void setService(ShauniService service) {
        this.service = (MontbsRunService) service;
    }
}
