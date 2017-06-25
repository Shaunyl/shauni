package com.fil.shauni.db.spring;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Chiara
 */
@Getter @RequiredArgsConstructor
public class MontbsDataKey {
    public final String host, database, tablespace;
}
