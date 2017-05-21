package com.fil.shauni.util.file;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Shaunyl
 */
@RequiredArgsConstructor
public class DefaultFilepath implements Filepath {
    @Getter @NonNull
    private String filepath;
    
    private String filename;

    @Override
    public DefaultFilepath replaceWildcard(String wc, Object value) {
        filepath = filepath.replaceAll(wc, value.toString());
        filename = filepath.substring(filepath.lastIndexOf('/'), filepath.length());
        return this;
    }

    @Override
    public String getFilename() {
        return filename;
    }
}
