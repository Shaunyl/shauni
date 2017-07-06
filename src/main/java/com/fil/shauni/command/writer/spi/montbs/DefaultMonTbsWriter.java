package com.fil.shauni.command.writer.spi.montbs;

import com.fil.shauni.command.writer.spi.montbs.config.MontbsWriterConfiguration;
import com.fil.shauni.command.writer.WriterConfiguration;
import com.fil.shauni.command.writer.WriterManager;
import com.fil.shauni.concurrency.pool.ThreadPoolManager;
import com.fil.shauni.db.spring.model.MontbsRunView;
import com.fil.shauni.db.spring.service.MontbsRunService;
import com.fil.shauni.db.spring.service.MontbsRunViewService;
import com.fil.shauni.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;
import java.sql.*;
import java.text.*;
import java.util.*;
import lombok.NonNull;

/**
 *
 * @author Filippo
 */
public class DefaultMonTbsWriter implements WriterManager {

    private final Writer rawWriter;

    private final PrintWriter printer;

    private boolean isCritical, isWarning = false;

    private final DecimalFormat formatter = new DecimalFormat("#,###.00");

    protected MontbsWriterConfiguration c;

    private List<MontbsRunView> lastRow;

    public DefaultMonTbsWriter(WriterConfiguration configuration) {
        this.c = (MontbsWriterConfiguration) configuration; //FIXME: use interfaces...
        this.rawWriter = configuration.getWriter();
        this.printer = new PrintWriter(rawWriter);
//        ThreadPoolManager.getInstance().execute(() -> {
//        SpringContext.getApplicationContext().getBean(MontbsRunViewService.class)
//                .findByHostName(c.getHost());
//        });
    }

    public void writeHeader() {
        writeNext("Tablespace Report\n");
        writeNext("Starting MonTbs on instance " + c.getInstance() + " at " + Sysdate.now(Sysdate.DASH_TIMEDATE));
        if (!c.isUndo()) {
            writeNext("Ignoring tablespace contents -> UNDO");
        }
        if (!c.getExclude().isEmpty()) {
            writeNext("Other tablespaces to ignore -> '" + String.join("', '", c.getExclude()) + "'");
        }
        writeNext("Thresholds used -> " + c.getWarning() + " (warning), " + c.getCritical() + " (critical)");
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

//        this.lastRow = SpringContext.getApplicationContext().getBean(MontbsRunViewService.class)
//                .findLastRun(c.getHost(), c.getInstance());

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

        if (csize > c.getCritical()) {
            if (!isCritical) {
                writeNext("Check for criticals:");
            }
            isCritical = true;
            retrieveTbsInfo(record);
            return;
        }

        if (csize > c.getWarning()) {
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
        String tablespace = record[1];

        String format = "yyyy-MM-dd HH:mm:ss..SS";

        Date sampleTime;
        try {
            sampleTime = new SimpleDateFormat(format).parse(Sysdate.now(format));
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }

        // Pay close attention to performance here.
        if (c.isGrowing()) {
            if (lastRow != null) {
//                MontbsRunView row = lastRow.stream()
//                        .filter(p -> p.getTablespaceName().equals(tablespace)).findFirst()
//                        .orElse(null);
//                if (row != null && row.getTotalUsedPercentage() < pct) {
//                    pattern += "  **" + Double.valueOf(new DecimalFormat("#.##")
//                            .format(pct - row.getTotalUsedPercentage()))
//                            + " [" + GeneralUtil.compareTwoTimeStamps(
//                                    new Timestamp(sampleTime.getTime()),
//                                    row.getSampleTime()) + "]";
//                }

//            MontbsRunView lastRun = getLastRun(c.getHost(), c.getInstance(), tablespace);
//            if (lastRun != null && lastRun.getTotalUsedPercentage() < pct) {
//                pattern += "  **" + Double.valueOf(new DecimalFormat("#.##")
//                        .format(pct - lastRun.getTotalUsedPercentage()))
//                        + " [" + GeneralUtil.compareTwoTimeStamps(
//                                new Timestamp(sampleTime.getTime()),
//                                lastRun.getSampleTime()) + "]";
//            }
            }

            String buffer = String.format(pattern, c.getInstance(), tablespace + "[" + size + "]", pct, "(" + free + ")");
            writeNext(new String[]{ buffer });

            if (c.isPersist()) {
                ThreadPoolManager.getInstance().execute(() -> {
                    SpringContext.getApplicationContext().getBean(MontbsRunService.class)
                            .persist(c.getHost(), c.getInstance(), tablespace, pct, new Timestamp(sampleTime.getTime()));
                });
            }
            // TRYME: save in a list of MontbsRun objects, than persist in batch for performance..
        }
    }

//    private MontbsRunView getLastRun(String host, String db, String tbs) {
//        return SpringContext.getApplicationContext().getBean(MontbsRunViewService.class)
//                .findFirstOrderBySampleTimeDesc(host, db, tbs);
//    }

    protected String convertToUnit(long bytes) {
        switch (c.getUnit()) {
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
}
