package com.fil.shauni.command.export;

import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.util.file.DefaultFilename;
import com.fil.shauni.util.file.Filename;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Chiara
 */
@Log4j2 @RequiredArgsConstructor
public class ExporterExtractor implements ResultSetExtractor<Integer> {
    
    final SpringExporter exporter;
    
    final Filename fn;
    
    @Override
    public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
        int rows = -1;
        try {
            rows = extract(rs);
        } catch (ShauniException ex) {
            log.error(ex.getMessage());
        }
        return rows;
    }

    public Integer extract(ResultSet rs) throws ShauniException {
        int rows = 0;
        try {
            rows = exporter.write(rs, fn);
        } catch (IOException ex) {
            throw new ShauniException(1005, "Error while writing data to the file " + fn.getPath() + "\n -> " + ex.getMessage());
        } catch (SQLException ex) {
            throw new ShauniException(1006, "Error while reading the result set\n -> " + ex.getMessage());
        }
        return rows;
    }
}
