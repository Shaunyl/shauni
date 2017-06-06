package com.fil.shauni.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
public class DatabaseUtil {

    private static final SimpleDateFormat TIMESTAMP_FORMATTER = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MMM-yyyy");

    private static final String BLOB_FORMATTER = "<BLOB>";

    public static String getTableSize(final Statement statement, final String schema, final String table, boolean scope) {
        String size = "";
        try {
            ResultSet rs = statement.executeQuery(String.format("SELECT bytes FROM %s"
                    + " WHERE segment_name = UPPER('%s') AND segment_type = 'TABLE'"
                    + " %s AND owner = UPPER('%s')", scope ? "user_segments" : "dba_segments", table, scope ? "--" : "", schema));

            while (rs.next()) {
                Double bytes = rs.getDouble(1);
                if (bytes > 1024) {
                    size = bytes / (double) 1024 + "KB";
                } else if (bytes > 104578) {
                    size = bytes / (double) 1048576 + "MB";
                } else if (bytes > 104578 * 1024) {
                    size = bytes / (double) (1048576 * 1024) + "GB";
                }
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 942) {
                throw new RuntimeException(ex.getMessage() + "Internal Error..", ex);
            }
        }
        return size;
    }

    public static String getColumnValue(ResultSet rs, int colType, int colIndex)
            throws SQLException, IOException {

        String value = "";

        switch (colType) {
            case Types.BIT:
                Object bit = rs.getObject(colIndex);
                if (bit != null) {
                    value = String.valueOf(bit);
                }
                break;
            case Types.BOOLEAN:
                boolean b = rs.getBoolean(colIndex);
                if (!rs.wasNull()) {
                    value = Boolean.toString(b);
                }
                break;
            case Types.CLOB:
                Clob c = rs.getClob(colIndex);
                if (c != null) {
                    value = GeneralUtil.readClob(c);
                }
                break;
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.REAL:
            case Types.NUMERIC:
                BigDecimal bd = rs.getBigDecimal(colIndex);
                if (bd != null) {
                    value = "" + bd.doubleValue();
                }
                break;
            case Types.INTEGER:
            case Types.TINYINT:
            case Types.SMALLINT:
                int intValue = rs.getInt(colIndex);
                if (!rs.wasNull()) {
                    value = "" + intValue;
                }
                break;
            case Types.JAVA_OBJECT:
                Object obj = rs.getObject(colIndex);
                if (obj != null) {
                    value = String.valueOf(obj);
                }
                break;
            case Types.DATE:
                java.sql.Date date = rs.getDate(colIndex);
                if (date != null) {
                    value = DATE_FORMATTER.format(date);
                }
                break;
            case Types.TIME:
                Time t = rs.getTime(colIndex);
                if (t != null) {
                    value = t.toString();
                }
                break;
            case Types.TIMESTAMP:
                Timestamp tstamp = rs.getTimestamp(colIndex);
                if (tstamp != null) {
                    value = TIMESTAMP_FORMATTER.format(tstamp);
                }
                break;
            case Types.LONGVARCHAR:
            case Types.VARCHAR:
            case Types.CHAR:
                value = rs.getString(colIndex);
                if (value != null) {
                    value = value.trim();
                }
                break;
            case Types.BLOB:
                value = BLOB_FORMATTER; // Not yet supported...
                break;
            default:
                value = "";
        }

        if (value == null) {
            value = "";
        }

        return value;
    }
}
