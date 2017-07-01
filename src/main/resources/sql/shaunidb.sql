/**
 * Author:  Filippo
 * Created: 25-giu-2017
 */

CREATE SCHEMA sha;

CREATE TABLE MontbsHostnames (
    host_id INT PRIMARY KEY,
    host_name VARCHAR(60) NOT NULL
);

CREATE TABLE MontbsDatabases (
    db_id INT PRIMARY KEY,
    db_name VARCHAR(30) NOT NULL
);

CREATE TABLE MontbsTablespaces (
    tablespace_id INT PRIMARY KEY,
    tablespace_name VARCHAR(30) NOT NULL
);

CREATE TABLE MontbsRuns (
    run_id INT NOT NULL,
    host_id INT NOT NULL,
    db_id INT NOT NULL,
    tablespace_id INT NOT NULL,
    total_used_pct DOUBLE NOT NULL,
    sample_time TIMESTAMP NOT NULL,
    CONSTRAINT montbs_runs_pk PRIMARY KEY (run_id),
    CONSTRAINT host_id_fk FOREIGN KEY (host_id) REFERENCES MontbsHostnames (host_id),
    CONSTRAINT db_id_fk FOREIGN KEY (db_id) REFERENCES MontbsDatabases (db_id),
    CONSTRAINT tablespace_id_fk FOREIGN KEY (tablespace_id) REFERENCES MontbsTablespaces (tablespace_id)
);