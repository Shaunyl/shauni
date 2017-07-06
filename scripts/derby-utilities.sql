/**
 * Author:  Filippo
 * Created: 5-lug-2017
 */

# Get total used pct trend of a tablespace
SELECT tablespace_name, sample_time, total_used_pct
FROM SHA.MONTBSRUNSVIEW
where tablespace_name = 'SYSAUX'
order by sample_time desc;

# Get size in bytes of a table
SELECT
    tableName,
    (select sum(numallocatedpages * pagesize) from new org.apache.derby.diag.SpaceTable('SHA', t.tablename) x) as size
FROM SYS.SYSTABLES t
WHERE tablename = 'MONTBSRUN'
ORDER BY size DESC;

# Retrieve execution plan of the following query
CALL SYSCS_UTIL.SYSCS_SET_RUNTIMESTATISTICS(1);

SELECT *
FROM MontbsRunsView
WHERE host_name = 'FILIPPO-PC'
AND db_name = 'XE' AND tablespace_name = 'SYSTEM'
ORDER BY sample_time DESC;

call SYSCS_UTIL.SYSCS_SET_RUNTIMESTATISTICS(0);
VALUES SYSCS_UTIL.SYSCS_GET_RUNTIMESTATISTICS();

SELECT sample_time, total_used_pct
FROM MontbsRunsView
WHERE sample_time in (SELECT MAX(sample_time) FROM MontbsRunsView GROUP BY tablespace_name)
  AND host_name = 'FILIPPO-PC' AND db_name = 'XE';