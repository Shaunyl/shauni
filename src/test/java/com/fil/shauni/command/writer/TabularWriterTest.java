package com.fil.shauni.command.writer;

import com.fil.shauni.command.writer.spi.export.TabularWriter;
import com.fil.shauni.util.StringUtils;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import static com.fil.shauni.util.StringUtils.*;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class TabularWriterTest {

    @Mock
    private ResultSet rs;

    @Mock
    private ResultSetMetaData md;

    private static StringWriter sw;

    private static TabularWriter writer;

    @Before
    public void setUpTest() throws Exception {
        sw = new StringWriter();
        writer = new TabularWriter(sw);

        when(rs.getInt(1)).thenReturn(1).thenReturn(2);
        when(rs.getString(2)).thenReturn("SYS").thenReturn("SYSTEM");
        when(rs.getString(3)).thenReturn("DEFAULT").thenReturn("DEFAULT");
        when(rs.getString(4)).thenReturn("10-Apr-2017").thenReturn("9-Apr-2017");
        when(rs.getMetaData()).thenReturn(md);

        when(md.getColumnName(1)).thenReturn("userid");
        when(md.getColumnName(2)).thenReturn("username");
        when(md.getColumnName(3)).thenReturn("profile");
        when(md.getColumnName(4)).thenReturn("created_date");

        when(md.getColumnCount()).thenReturn(4);
        when(md.getColumnType(anyInt())).thenReturn(Types.VARCHAR);
        when(md.getColumnType(1)).thenReturn(Types.INTEGER);
    }

    @Test
    public void testRetrieveOneLineNoHeader() throws SQLException, IOException {
        when(rs.next()).thenReturn(true, false);
        int lines = writer.writeAll(rs, false);
        verify(md, times(0)).getColumnName(any(Integer.class));
        Assert.assertEquals(1, lines);
    }

    @Test
    public void testRetrieveOneLineWithHeader() throws SQLException, IOException {
        when(rs.next()).thenReturn(true, false);
        int lines = writer.writeAll(rs, true);
        verify(md, times(4)).getColumnName(any(Integer.class));
        verify(rs, times(3)).getString(any(Integer.class));
        Assert.assertEquals(1, lines);
    }

    @Test
    public void testRetrieveTwoLinesNoHeader() throws SQLException, IOException {
        when(rs.next()).thenReturn(true, true, false);
        int lines = writer.writeAll(rs, false);
        verify(md, times(0)).getColumnName(any(Integer.class));
        Assert.assertEquals(2, lines);
    }

    @Test
    public void testTabularOutputOneLineNoHeader() throws SQLException, IOException {
        when(rs.next()).thenReturn(true, false);
        int w = DefaultWriter.COLUMN_WIDTH;
        String dw = "%-" + w + "s";
        writer.writeAll(rs, false);
        verify(md, times(0)).getColumnName(any(Integer.class));

        String expected = String.format(dw + dw + dw + dw + "\n", "1", "SYS", "DEFAULT", "10-Apr-2017");
        Assert.assertEquals(expected, sw.toString());
    }

    @Test
    public void testTabularOutputOneLineWithHeaderAndColformats() throws SQLException, IOException {
        TreeMap<String, Integer> colformats = new TreeMap<>();
        int widthCol1 = 7;
        int widthCol2 = 10;
        colformats.put("userid", widthCol1);
        colformats.put("username", widthCol2);
        writer = new TabularWriter(sw, colformats);
        
        when(rs.next()).thenReturn(true, false);
        int w = DefaultWriter.COLUMN_WIDTH;
        String dw = "%-" + w + "s";
        writer.writeAll(rs, true);
        verify(md, times(4)).getColumnName(any(Integer.class));
        verify(rs, times(3)).getString(any(Integer.class));

        String pattern = "%-" + widthCol1 + "s %-" + widthCol2 + "s " + dw + " " + dw + "\n";
        String header = String.format(pattern, "userid", "username", "profile", "created_date");
        String separator = String.format(pattern, repeat("-", widthCol1), repeat("-", widthCol2), repeat("-", w), repeat("-", w));
        String data = String.format(pattern, "1", "SYS", "DEFAULT", "10-Apr-2017");
        Assert.assertEquals(header + separator + data, sw.toString());
    }
    
    @Test
    public void testTabularOutputOneLineWithHeader() throws SQLException, IOException {
        when(rs.next()).thenReturn(true, false);
        int w = DefaultWriter.COLUMN_WIDTH;
        String dw = "%-" + w + "s";
        writer.writeAll(rs, true);
        verify(md, times(4)).getColumnName(any(Integer.class));
        verify(rs, times(3)).getString(any(Integer.class));

        String sep = StringUtils.repeat("-", w);
        String header = String.format(dw + " " + dw + " " + dw + " " + dw + "\n", "userid", "username", "profile", "created_date");
        String separator = Stream.generate(() -> sep).limit(4).collect(Collectors.joining(" ")) + "\n";
        String data = String.format(dw + " " + dw + " " + dw + " " + dw + "\n", "1", "SYS", "DEFAULT", "10-Apr-2017");
        Assert.assertEquals(header + separator + data, sw.toString());
    }

    @Test
    public void testTabularOutputTwoLinesWithHeader() throws SQLException, IOException {
        when(rs.next()).thenReturn(true, true, false);
        int w = DefaultWriter.COLUMN_WIDTH;
        String dw = "%-" + w + "s";
        writer.writeAll(rs, true);
        verify(md, times(4)).getColumnName(any(Integer.class));
        verify(rs, times(6)).getString(any(Integer.class));

        String sep = StringUtils.repeat("-", w);
        String header = String.format(dw + " " + dw + " " + dw + " " + dw + "\n", "userid", "username", "profile", "created_date");
        String separator = Stream.generate(() -> sep).limit(4).collect(Collectors.joining(" ")) + "\n";
        String data = String.format(dw + " " + dw + " " + dw + " " + dw + "\n", "1", "SYS", "DEFAULT", "10-Apr-2017");
        data += String.format(dw + " " + dw + " " + dw + " " + dw + "\n", "2", "SYSTEM", "DEFAULT", "9-Apr-2017");
        Assert.assertEquals(header + separator + data, sw.toString());
    }

    @After
    public void tearDownTest() throws Exception {
        sw.close();
        writer.close();
    }
}
