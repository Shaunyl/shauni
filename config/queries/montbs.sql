SELECT (SELECT name FROM v$database) database_name, d.tablespace_name
  , a.bytes size_bytes, a.bytes - DECODE(f.bytes, NULL, 0, f.bytes) used_bytes
  , a.bytes - (a.bytes - DECODE(f.bytes, NULL, 0, f.bytes)) free_bytes
  , TO_CHAR((((a.bytes - DECODE(f.bytes, NULL, 0, f.bytes)) / 1048576) * 100) / (a.bytes / 1048576), '999.99') used_pct
  , SYSDATE last_updated
FROM sys.dba_tablespaces d, sys.sm$ts_avail a, sys.sm$ts_free f
WHERE d.tablespace_name = a.tablespace_name
  AND f.tablespace_name(+) = d.tablespace_name
  AND a.tablespace_name NOT IN (&1)
  AND d.contents NOT IN (&2)
  AND (((a.bytes - DECODE(f.bytes, NULL, 0, f.bytes)) / 1048576) * 100) / (a.bytes / 1048576) > &3
ORDER BY 6 DESC;

-- examples
-- row  9# = ('TBS1', 'TBS2', ..)
-- row 10# = ('UNDO')
-- row 11# = 80