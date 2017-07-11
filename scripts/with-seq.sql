/**
 * Author:  Filippo
 * Created: 11-lug-2017
 */
CREATE SCHEMA sha;

CREATE TABLE montbsruns (
    run_id INT PRIMARY KEY,
    host_name VARCHAR(60) NOT NULL,
    db_name VARCHAR(30) NOT NULL,
    tablespace_name VARCHAR(30) NOT NULL,
    total_used_pct DOUBLE NOT NULL,
    sample_time TIMESTAMP NOT NULL
);

CREATE SEQUENCE montbsruns_seq INCREMENT BY 1 START WITH 1;

-- INSERT INTO montbsruns VALUES ();