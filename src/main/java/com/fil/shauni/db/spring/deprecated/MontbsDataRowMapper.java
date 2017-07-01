//package com.fil.shauni.db.spring;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import lombok.NoArgsConstructor;
//import org.springframework.jdbc.core.RowMapper;
//
///**
// *
// * @author Filippo
// */
//@NoArgsConstructor
//public class MontbsDataRowMapper implements RowMapper<MontbsRun> {
//    @Override
//    public MontbsRun mapRow(ResultSet rs, int rowNum) throws SQLException {
//        MontbsRunKey key = new MontbsRunKey(rs.getString(3), rs.getString(4), rs.getString(5));
//        MontbsRun montbs = new MontbsRun(rs.getInt(1), rs.getInt(2), key);
//        montbs.setTotalUsedPercentage(rs.getDouble(6));
//        montbs.setSampleTime(rs.getTimestamp(7));
//        return montbs;
//    }    
//}
