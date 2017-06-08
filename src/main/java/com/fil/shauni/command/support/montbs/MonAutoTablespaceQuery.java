package com.fil.shauni.command.support.montbs;

import java.util.List;

/**
 *
 * @author Filippo
 */
public class MonAutoTablespaceQuery implements TablespaceQuery {
    @Override
    public String prepare(List<String> exclude, boolean undo, int pct_usage_threshold) {
        return "SELECT (SELECT name FROM v$database) database_name, d.tablespace_name"
                + " , d.status status, a.bytes size_mb,"
                + "  TO_NUMBER(DECODE(m.aut_max_mb, 0, NULL, m.aut_max_mb)) max_mb,"
                + "  (a.bytes - DECODE(f.bytes, NULL, 0, f.bytes)) / 1 used_mb,"
                + "  (a.bytes / 1 - (a.bytes - DECODE(f.bytes, NULL, 0, f.bytes)) / 1) free_mb,"
                + "  ROUND(((a.bytes - DECODE(f.bytes, NULL, 0, f.bytes)) * 100) / a.bytes) used_pct"
                + " , m.autoextensible,"
                + "  DECODE(ROUND(((a.bytes - DECODE(f.bytes, NULL, 0, f.bytes)) / 1) / TO_NUMBER(DECODE(m.aut_max_mb, 0, NULL, m.aut_max_mb)) * 100), null, -1,"
                + "    ROUND(((a.bytes - DECODE(f.bytes, NULL, 0, f.bytes)) / 1) / TO_NUMBER(DECODE(m.aut_max_mb, 0, NULL, m.aut_max_mb)) * 100)) auto_used_pct"
                + " FROM sys.dba_tablespaces d, sys.sm$ts_avail a, sys.sm$ts_free f,"
                + "  (SELECT x.tablespace_name, SUM(y.maxbytes / 1) aut_max_mb, SUM(DECODE(y.autoextensible, 'NO', 1, 0)) autoextensible"
                + "   FROM sys.dba_tablespaces x, sys.dba_data_files y"
                + "   WHERE x.tablespace_name = y.tablespace_name"
                + "   GROUP BY x.tablespace_name) m"
                + " WHERE d.tablespace_name = a.tablespace_name"
                + "  AND f.tablespace_name(+) = d.tablespace_name"
                + "  AND d.tablespace_name = m.tablespace_name"
                + (exclude.isEmpty() ? "--" : "") + " AND a.tablespace_name NOT IN ('" + String.join("', '", exclude) + "')\n"
                + (undo ? "--" : "") + " AND d.contents != 'UNDO'\n"
                + " AND ROUND(((a.bytes - DECODE(f.bytes, NULL, 0, f.bytes)) * 100) / a.bytes) > " + pct_usage_threshold + "\n"
                + " ORDER BY 10 DESC";
        
        // FIXME: create a functional interface to do (condition) ? "--" : ""
    }
}
