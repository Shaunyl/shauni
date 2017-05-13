package com.fil.shauni.command;

/**
 *
 * @author Shaunyl
 */
public class DatabaseConfiguration {

    private final static String CDIR = "config/";

    private final static String SINGLEDB_CONN_FILE = "singledb.cfg";

    private final static String MULTIDB_CONN_FILE = "multidb.cfg";

    public final static String SINGLEDB_CONN = CDIR + SINGLEDB_CONN_FILE;

    public final static String MULTIDB_CONN = CDIR + MULTIDB_CONN_FILE;
    
    private final static String MULTIDB_CONN_ENCRYPTED_FILE = "multidb.cry";

    public final static String MULTIDB_CONN_ENCRYPTED = CDIR + MULTIDB_CONN_ENCRYPTED_FILE;
}
