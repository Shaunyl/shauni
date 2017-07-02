/**
 * Author:  Filippo
 * Created: 25-giu-2017
 */

CREATE SCHEMA sha;

CREATE TABLE MontbsHostnames (
    host_id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    host_name VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE MontbsDatabases (
    db_id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    db_name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE MontbsTablespaces (
    tablespace_id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    tablespace_name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE MontbsRuns (
    run_id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    host_id INT NOT NULL,
    db_id INT NOT NULL,
    tablespace_id INT NOT NULL,
    total_used_pct DOUBLE NOT NULL,
    sample_time TIMESTAMP NOT NULL,
    CONSTRAINT host_id_fk FOREIGN KEY (host_id) REFERENCES MontbsHostnames (host_id),
    CONSTRAINT db_id_fk FOREIGN KEY (db_id) REFERENCES MontbsDatabases (db_id),
    CONSTRAINT tablespace_id_fk FOREIGN KEY (tablespace_id) REFERENCES MontbsTablespaces (tablespace_id)
);

CREATE VIEW MontbsRunsView (run_id, host_name, db_name, tablespace_name, total_used_pct, sample_time)
    AS
        SELECT r.run_id, h.host_name, d.db_name, t.tablespace_name, r.total_used_pct, r.sample_time
        FROM MontbsRuns r, MontbsHostnames h, MontbsDatabases d, MontbsTablespaces t
        WHERE r.host_id = h.host_id
            AND r.db_id = d.db_id
            AND r.tablespace_id = t.tablespace_id
        ORDER BY r.run_id;