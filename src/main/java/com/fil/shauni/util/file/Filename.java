package com.fil.shauni.util.file;

/**
 *
 * @author Chiara
 */
public interface Filename {
    String getName();
    String getPath();
    Filename replaceWildcard(String wildcard, Object value);
}
