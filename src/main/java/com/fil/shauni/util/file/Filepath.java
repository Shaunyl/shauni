package com.fil.shauni.util.file;

/**
 *
 * @author Chiara
 */
public interface Filepath {
    String getFilepath();
    String getFilename();
    Filepath replaceWildcard(String wildcard, Object value);
}
