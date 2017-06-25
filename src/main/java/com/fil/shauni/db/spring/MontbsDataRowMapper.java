package com.fil.shauni.db.spring;

import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Filippo
 */
@NoArgsConstructor
public class MontbsDataRowMapper implements RowMapper<MontbsData> {
    @Override
    public MontbsData mapRow(ResultSet rs, int rowNum) throws SQLException {
        MontbsDataKey key = new MontbsDataKey(rs.getString(3), rs.getString(4), rs.getString(5));
        MontbsData montbs = new MontbsData(rs.getInt(1), rs.getInt(2), key);
        montbs.setTotalUsedPercentage(rs.getDouble(6));
        montbs.setSampleTime(rs.getTimestamp(7));
        return montbs;
    }    
}
