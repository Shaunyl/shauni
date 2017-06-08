package com.fil.shauni.command.support.montbs;

import java.util.List;

/**
 *
 * @author Filippo
 */
public class MonTablespaceQuery implements TablespaceQuery {

    @Override
    public String prepare(List<String> exclude, boolean undo, int pct_usage_threshold) {
        return "SELECT (SELECT name FROM v$database) database_name, d.tablespace_name\n"
                + " , a.bytes size_bytes, a.bytes - DECODE(f.bytes, NULL, 0, f.bytes) used_bytes\n"
                + " , a.bytes - (a.bytes - DECODE(f.bytes, NULL, 0, f.bytes)) free_bytes\n"
                + " , TO_CHAR((((a.bytes - DECODE(f.bytes, NULL, 0, f.bytes)) / 1048576) * 100) / (a.bytes / 1048576), '999.99') used_pct\n"
                + " , SYSDATE last_updated\n"
                + " FROM sys.dba_tablespaces d, sys.sm$ts_avail a, sys.sm$ts_free f\n"
                + " WHERE d.tablespace_name = a.tablespace_name\n"
                + " AND f.tablespace_name(+) = d.tablespace_name\n"
                + (exclude.isEmpty() ? "--" : "") + " AND a.tablespace_name NOT IN ('" + String.join("', '", exclude) + "')\n"
                + (undo ? "--" : "") + " AND d.contents != 'UNDO'\n"
                + " AND (((a.bytes - DECODE(f.bytes, NULL, 0, f.bytes)) / 1048576) * 100) / (a.bytes / 1048576) > " + pct_usage_threshold + "\n"
                + " ORDER BY 6 DESC";
    }
}
