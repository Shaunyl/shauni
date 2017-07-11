/**
 * Author:  Filippo
 * Created: 2-lug-2017
 */
CREATE SCHEMA sha;

CREATE TABLE MontbsHostnames (
    host_id PRIMARY KEY,
    host_name VARCHAR(60) NOT NULL UNIQUE
);

CREATE SEQUENCE hostnames_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE MontbsDatabases (
    db_id INT PRIMARY KEY,
    db_name VARCHAR(30) NOT NULL UNIQUE
);

CREATE SEQUENCE databases_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE MontbsTablespaces (
    tablespace_id INT PRIMARY KEY,
    tablespace_name VARCHAR(30) NOT NULL UNIQUE
);

CREATE SEQUENCE tablespaces_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE MontbsRuns (
    run_id INT PRIMARY KEY,
    host_id INT,
    db_id INT,
    tablespace_id INT,
    total_used_pct DOUBLE NOT NULL,
    sample_time TIMESTAMP NOT NULL
-- ,
--     CONSTRAINT host_id_fk FOREIGN KEY (host_id) REFERENCES MontbsHostnames (host_id),
--     CONSTRAINT db_id_fk FOREIGN KEY (db_id) REFERENCES MontbsDatabases (db_id),
--     CONSTRAINT tablespace_id_fk FOREIGN KEY (tablespace_id) REFERENCES MontbsTablespaces (tablespace_id)
);

CREATE SEQUENCE runs_seq INCREMENT BY 1 START WITH 1;

CREATE VIEW MontbsRunsView (run_id, host_name, db_name, tablespace_name, total_used_pct, sample_time)
    AS
        SELECT r.run_id, h.host_name, d.db_name, t.tablespace_name, r.total_used_pct, r.sample_time
        FROM MontbsRuns r, MontbsHostnames h, MontbsDatabases d, MontbsTablespaces t
        WHERE r.host_id = h.host_id
            AND r.db_id = d.db_id
            AND r.tablespace_id = t.tablespace_id
        ORDER BY r.run_id;

INSERT INTO MontbsHostnames (host_name) VALUES ('FILIPPO-PC');
INSERT INTO MontbsHostnames (host_name) VALUES ('localhost');

INSERT INTO MontbsDatabases (db_name) VALUES ('XE');
INSERT INTO MontbsDatabases (db_name) VALUES ('ERMDB');

INSERT INTO MontbsTablespaces (tablespace_name) VALUES ('SYSTEM');
INSERT INTO MontbsTablespaces (tablespace_name) VALUES ('SYSAUX');
INSERT INTO MontbsTablespaces (tablespace_name) VALUES ('TEMP');
INSERT INTO MontbsTablespaces (tablespace_name) VALUES ('UNDOTBS1');

INSERT INTO MontbsRuns (host_id, db_id, tablespace_id, total_used_pct, sample_time) VALUES (1, 1, 1, 1.10, '2017-02-05 15:48:10');
INSERT INTO MontbsRuns (host_id, db_id, tablespace_id, total_used_pct, sample_time) VALUES (1, 1, 1, 2.87, '2017-02-06 15:45:06');
INSERT INTO MontbsRuns (host_id, db_id, tablespace_id, total_used_pct, sample_time) VALUES (1, 1, 1, 5.12, '2017-02-22 15:48:24');
INSERT INTO MontbsRuns (host_id, db_id, tablespace_id, total_used_pct, sample_time) VALUES (1, 1, 1, 15.12, '2017-02-23 15:48:24');
INSERT INTO MontbsRuns (host_id, db_id, tablespace_id, total_used_pct, sample_time) VALUES (2, 1, 1, 25.12, '2017-02-21 15:48:24');
INSERT INTO MontbsRuns (host_id, db_id, tablespace_id, total_used_pct, sample_time) VALUES (1, 1, 2, 35.12, '2017-02-06 15:48:24');
INSERT INTO MontbsRuns (host_id, db_id, tablespace_id, total_used_pct, sample_time) VALUES (1, 1, 2, 45.12, '2017-02-05 15:48:24');
INSERT INTO MontbsRuns (host_id, db_id, tablespace_id, total_used_pct, sample_time) VALUES (1, 1, 3, 55.12, '2017-02-04 15:48:24');
INSERT INTO MontbsRuns (host_id, db_id, tablespace_id, total_used_pct, sample_time) VALUES (1, 1, 4, 65.12, '2017-02-03 15:48:24');
