/**
 * Author:  Filippo
 * Created: 1-lug-2017
 */
INSERT INTO MontbsHostnames (host_id, host_name) VALUES (1, 'FILIPPO-PC');
INSERT INTO MontbsHostnames (host_id, host_name) VALUES (2, 'localhost');
INSERT INTO MontbsDatabases (db_id, db_name) VALUES (1, 'XE');
INSERT INTO MontbsDatabases (db_id, db_name) VALUES (2, 'ERMDB');

INSERT INTO MontbsTablespaces (tablespace_id, tablespace_name) VALUES (1, 'SYSTEM');
INSERT INTO MontbsTablespaces (tablespace_id, tablespace_name) VALUES (2, 'SYSAUX');
INSERT INTO MontbsTablespaces (tablespace_id, tablespace_name) VALUES (3, 'TEMP');

INSERT INTO MontbsRuns VALUES (1, 1, 1, 1, 1.10, '2017-02-05 15:48:10');
INSERT INTO MontbsRuns VALUES (2, 1, 1, 1, 2.87, '2017-02-06 15:45:06');
INSERT INTO MontbsRuns VALUES (3, 1, 1, 1, 5.12, '2017-02-05 15:48:24');
