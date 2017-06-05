package com.fil.shauni.io.spi;

import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.io.FileManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Filippo
 */
public class ParFileManager implements FileManager {

    @Override
    public int count(String filename) {
        return this.r(filename).size();
    }

    @Override
    public String read(String filename, String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private List<String> r(String filename) {
        List<String> parfile = new ArrayList<>();

        try (BufferedReader buffer = Files.newBufferedReader(Paths.get(filename))) {
            String line;
            String entry = buffer.readLine();
            if (entry.length() > 0 && !entry.trim().startsWith("#") && entry.matches("(\\w+\\=.+)")) {
                while ((line = buffer.readLine()) != null) {
                    if (line.length() > 0 && !line.trim().startsWith("#")) {
                        if (line.matches("(\\w+\\=.+)")) {
                            parfile.add("-" + entry);
                            entry = line;
                        } else {
                            entry += " " + line.trim();
                        }
                    }
                }
                parfile.add("-" + entry.trim());
            }
        } catch (IOException e) {
            throw new ShauniException(600, "Could not read file " + filename + ": " + e);
        }
        return parfile;
    }

    @Override
    public String[] readWithKeys(String filename, String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> readAll(String filename) {
        return this.r(filename);
    }

    @Override
    public Map<String, String> readAllWithKeys(String filename, String keyPrefix) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, String[]> readAllWithCompositeKeys(String filename, char splitter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
