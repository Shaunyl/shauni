/**
 * Author:  Filippo
 * Created: 29-giu-2017
 */

CREATE TABLE MontbsTablespaces (
    tbs_key_id INT PRIMARY KEY,
    host_name VARCHAR(60) NOT NULL,
    db_name VARCHAR(8) NOT NULL,
    tablespace_name VARCHAR(30) NOT NULL
);

CREATE TABLE MontbsRuns (
    tbs_run_id INT NOT NULL,
    tbs_key_id INT NOT NULL,
    total_used_pct DOUBLE NOT NULL,
    sample_time DATE NOT NULL,
    CONSTRAINT montbs_runs_key PRIMARY KEY (tbs_run_id, tbs_key_id),
    CONSTRAINT tbs_key_id_fk FOREIGN KEY (tbs_key_id) REFERENCES MontbsTablespaces (tbs_key_id)
);

/*

MANY TO ONE

multiple tbs_key_id in MontbsRuns which reference one tbs_key_id in MontbsTablespaces

*/