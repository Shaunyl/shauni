
import static com.fil.shauni.Main.beanFactory;
import com.fil.shauni.command.export.DefaultExporter;
import com.fil.shauni.command.export.TabularExporter;
import com.fil.shauni.command.writer.TabularWriter;
import com.fil.shauni.db.pool.JDBCPoolManager;
import com.fil.shauni.util.file.ExportTableFilename;
import com.fil.shauni.util.file.Filename;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

//import static org.powermock.api.easymock.PowerMock.createMock;
/**
 *
 * @author Chiara
 */
@RunWith(MockitoJUnitRunner.class)
public class ExportTestMock extends TestCase {

    @Mock
    private DataSource ds;

    @Mock
    private Connection c;

    @Mock
    private Statement statement;

    @Mock
    private ResultSet rs;

    @Mock
    private JDBCPoolManager pool;

    @Before @Override
    public void setUp() throws Exception {
        assertNotNull(ds);
        when(c.createStatement()).thenReturn(statement);
        when(ds.getConnection()).thenReturn(c);

        when(rs.first()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(98);
        when(rs.getString(2)).thenReturn("Filippo");
        when(rs.getString(3)).thenReturn("Testino");
        when(statement.executeQuery(any(String.class))).thenReturn(rs);
    }

    @Test
    public void testTabularExporter() throws Exception {

        final List<String[]> row = new ArrayList<>(1);

        when(rs.next()).thenReturn(true).thenReturn(false);
        while (rs.next()) {
            String id = String.valueOf(rs.getInt(1));
            String name = rs.getString(2);
            String surname = rs.getString(3);
            row.add(new String[]{id, name, surname});
        }

        DefaultExporter cmd = new DefaultExporter() {
            @Override
            public int write(ResultSet rs, Filename filename) throws SQLException, IOException {
                TabularWriter writer = new TabularWriter(new FileWriter(filename.getPath() + ".txt"));

                writer.writeAll(row);
                writer.close();
                return row.size();
            }
        };

        cmd.setDirectory("out");
        cmd.setFilename("%i-%t-%d");
        cmd.setFormat("tabular");
        List<String> tables = new ArrayList<>();
        tables.add("SYS.DBA_USERS");
        cmd.setTables(tables);
//        cmd.createStatement(c, 100);
        cmd.validate();
        cmd.setup();
        cmd.export(0, new Object[]{"SYS.DBA_USERS"});
        
        String file = FileUtils.readFileToString(new File("out/%i-SYS.DBA_USERS-290417-1823.txt")).trim();
        assertEquals(file, "98                        Filippo                   Testino                   ".trim());
    }

    @Test @Ignore
    public void testExportTable() {

        int parallel = 2;
        List<String> tables = new ArrayList<>();
//        tables.add("SELECT username, created FROM sys.dba_users");
//        tables.add("SYSTEM.REPCAT$_REPCAT");
        tables.add("SYS.DBA_USERS");
        tables.add("SYS.V_$INSTANCE");
        String format = "tabular";
        String filename = "out/%i-%t-%d.txt";
        Map<String, Integer> colformats = new HashMap<>();
        colformats.put("username", 120);
//
//        DefaultExporter cmd = beanFactory.getBean(TabularExporter.class);
////        DefaultExporterControl cmd = new TabularExporterControl();
//        cmd.setParallel(parallel);
//        cmd.setTables(tables);
////        cmd.setQueries(tables);
//        cmd.setFormat(format);
//        cmd.setFilename(filename);
//        ((TabularExporter) cmd).setColformats(colformats);
//
//        cmd.execute();
    }
}
