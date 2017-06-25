/**
 * Author:  Filippo
 * Created: 25-giu-2017
 */

CREATE SCHEMA sha;

CREATE TABLE MontbsRuns (
    run_id INT NOT NULL,
    tbs_run_id INT NOT NULL,
    host_name VARCHAR(60) NOT NULL,
    db_name VARCHAR(8) NOT NULL,
    tablespace_name VARCHAR(30) NOT NULL,
    total_used_pct DOUBLE NOT NULL,
    sample_time TIMESTAMP NOT NULL
);

ALTER TABLE Sha.MontbsRuns ADD PRIMARY KEY (run_id, tbs_run_id);

CREATE TABLE MontbsRunMessages (
    run_id INT NOT NULL,
    tbs_run_id INT NOT NULL,
    delta_used_pct DOUBLE NOT NULL,
    CONSTRAINT montbs_run_id_key FOREIGN KEY (run_id, tbs_run_id) REFERENCES MontbsRuns(run_id, tbs_run_id)
);
