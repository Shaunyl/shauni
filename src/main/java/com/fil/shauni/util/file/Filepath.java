package com.fil.shauni.util.file;

/**
 *
 * @author Chiara
 */
public interface Filepath {
    String getFilepath();
    String getFilename();
    String getAbsoluteFilePath();
    Filepath replaceWildcard(String wildcard, Object value);
}
