package com.fil.shauni.command.support;

/**
 *
 * @author Shaunyl
 */
public final class Query {

    public static String getTablespacesAllocation(String exclude, String nocontent, int warning, String commentTBS, String commentUNDO) {
        return "SELECT (SELECT name FROM v$database) database_name, d.tablespace_name\n"
                + "  , a.bytes size_bytes, a.bytes - DECODE(f.bytes, NULL, 0, f.bytes) used_bytes\n"
                + "  , a.bytes - (a.bytes - DECODE(f.bytes, NULL, 0, f.bytes)) free_bytes\n"
                + "  , TO_CHAR((((a.bytes - DECODE(f.bytes, NULL, 0, f.bytes)) / 1048576) * 100) / (a.bytes / 1048576), '999.99') used_pct\n"
                + "  , SYSDATE last_updated\n"
                + "FROM sys.dba_tablespaces d, sys.sm$ts_avail a, sys.sm$ts_free f\n"
                + "WHERE d.tablespace_name = a.tablespace_name\n"
                + "  AND f.tablespace_name(+) = d.tablespace_name\n"
                + commentTBS + "  AND a.tablespace_name NOT IN " + exclude + "\n"
                + commentUNDO + "  AND d.contents != " + nocontent + "\n"
                + "  AND (((a.bytes - DECODE(f.bytes, NULL, 0, f.bytes)) / 1048576) * 100) / (a.bytes / 1048576) > " + warning + "\n"
                + "ORDER BY 6 DESC";
    }
    
    public static String getTotalPGAUsed() {
        return "SELECT inst_id, ROUND(SUM(pga_used_mb), 2) pga_used_mb, type"
                + "--, ROUND(SUM(alloc_mb), 2) alloc_mb, ROUND(SUM(free_mb), 2) free_mb, ROUND(SUM(max_mb), 2) max_mb\n"
                + "FROM (\n"
                + "SELECT s.inst_id, s.type, pga_used_mem / 1048576 pga_used_mb"
                + "--, PGA_ALLOC_MEM / 1048576 alloc_mb, PGA_FREEABLE_MEM / 1048576 free_mb, PGA_MAX_MEM / 1048576 max_mb\n"
                + "FROM gv$session s JOIN gv$process p ON s.paddr = p.addr\n"
                + "ORDER BY last_call_et ASC)\n"
                + "GROUP BY inst_id, type";
    }
}
