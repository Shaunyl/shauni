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