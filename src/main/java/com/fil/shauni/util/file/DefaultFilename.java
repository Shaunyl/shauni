package com.fil.shauni.util.file;

import lombok.Getter;

/**
 *
 * @author Shaunyl
 */
public class DefaultFilename implements Filename {
    @Getter
    private String path, name;
   
    public DefaultFilename(String path, String name) {
        this.path = path;
        this.name = name;
    }

    @Override
    public DefaultFilename replaceWildcard(String wc, Object value) {
        path = path.replaceAll(wc, value.toString());
        setName();
        return this;
    }

    private void setName() {
        name = path.substring(path.lastIndexOf('/'), path.length());
    }
}
