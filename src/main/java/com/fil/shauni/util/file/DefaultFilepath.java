package com.fil.shauni.util.file;

import java.io.File;
import lombok.Getter;
import lombok.NonNull;

/**
 *
 * @author Shaunyl
 */
public class DefaultFilepath implements Filepath {
    @Getter
    private String filepath;
    
    private String filename;

    public DefaultFilepath(final @NonNull String filepath) {
        this.filepath = filepath;
        this.filename = getFilenameFromFilePath(filepath);
    }

    @Override
    public DefaultFilepath replaceWildcard(String wc, Object value) {
        filepath = filepath.replaceAll(wc, value.toString());
        filename = getFilenameFromFilePath(filepath);
        return this;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getAbsoluteFilePath() {
        return new File(filepath).getAbsolutePath();
    }
    
    private String getFilenameFromFilePath(String f) {
        return f.substring(f.lastIndexOf('/'), f.length());
    }
}
