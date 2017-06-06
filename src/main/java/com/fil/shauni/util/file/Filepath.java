package com.fil.shauni.util.file;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
public interface Filepath {
    String getFilepath();
    String getFilename();
    String getAbsoluteFilePath();
    Filepath replaceWildcard(String wildcard, Object value);
}
