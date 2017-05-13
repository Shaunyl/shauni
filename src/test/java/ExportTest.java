
import com.fil.shauni.command.DatabaseCommandControl;
import com.fil.shauni.command.export.DefaultExporter;
import com.fil.shauni.command.export.TabularExporter;
import com.fil.shauni.mainframe.ui.CommandLinePresentationControl;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Shaunyl
 */
@Log4j
public class ExportTest {

    private final static BeanFactory beanFactory;

    static {
//        beanFactory = new ClassPathXmlApplicationContext("/beans/Beans.xml");
        beanFactory = new ClassPathXmlApplicationContext("file:src/main/resources/beans/Beans.xml");
    }

//    @Test
    public void getHelp() {
        String[] args = new String[] { "exp", "--help" };
        CommandLinePresentationControl cliControl = beanFactory.getBean(CommandLinePresentationControl.class);
//        cliControl.executeCommand(args);
    }

    @BeforeClass
    public static void setUpClass() {
//        beanFactory = new ClassPathXmlApplicationContext("/beans/Beans.xml");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

//    @Test
    public void testWriter() {
//        WriterManager mockWriter = Mockito.mock(WriterManager.class);
//        
//        DefaultExporterControl mockExporter = beanFactory.getBean(DefaultExporterControl.class, mockWriter);
//        
//        ResultSet rs = null;
//        Filename f = null;
//        mockExporter.write(rs, f);
    }

//    @Test
//    public void exportOneTableSerialTabular() {
//        int parallel = 1;
//        Set<Object> tables = new HashSet<>();
//        tables.add("SYS.DBA_USERS");
//        String format = "tabular";
//    String filename = "exportOneTableSerialTabular.txt";
//        Map<String, Integer> colformats = new HashMap<>();
//        colformats.put("username", 20);
//        
//        DefaultExporterControl cmd = new TabularExporterControl();
//        cmd.setParallel(parallel);
//        cmd.setTables(tables);
//        cmd.setFormat(format);
//        cmd.setFilename(filename);
//        ((TabularExporterControl)cmd).setColformats(colformats);
//        
//        cmd.execute();
//    }
//    
//    @Test
    public void exportOneQuerySerial() {
        int parallel = 1;
        int batchSize = 1;
        String format = "tabular";
        String filename = "out/%i-%d.txt";
        List<String> queries = new ArrayList<>(batchSize);
        queries.add("SELECT * FROM dual");

        Map<String, Integer> colformats = new HashMap<>();
        colformats.put("username", 30);

//        DefaultExporter cmd = beanFactory.getBean(TabularExporter.class);
        // The strig name of the bean is the name of the command!
        // But I have only one command "exp" and different formats.. so?
        DatabaseCommandControl cmd = (DatabaseCommandControl) beanFactory.getBean("exptab");
//        cmd.setParallel(parallel);
//        cmd.setQueries(queries);
//        cmd.setFormat(format);
//        cmd.setFilename(filename);
        ((TabularExporter) cmd).setColformats(colformats);

        cmd.execute();
    }

    @Test
    public void exportFourTablesParallelTabular() {
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

//        DefaultExporter cmd = beanFactory.getBean(TabularExporter.class);
//        DefaultExporterControl cmd = new TabularExporterControl();
//        cmd.setParallel(parallel);
//        cmd.setTables(tables);
//        cmd.setQueries(tables);
//        cmd.setFormat(format);
//        cmd.setFilename(filename);
//        ((TabularExporter) cmd).setColformats(colformats);

//        cmd.execute();
    }

//    @Test
    public void example() {
//        final WriterControl mockWriter = Mockito.mock(TabularWriterControl.class);
//        final WriterControl wcMock = spy(new TabularWriterControl());
//
//        DefaultExporterControl exporter = new TabularExporterControl(){
//            @Override
//            public int write(ResultSet rs, Filename filename) {
//                setRset(rs);
//                setFn(filename);
//                return mockWriter.write(rs, filename);
//            }
//        };
// 
//        
//        int parallel = 1;
//        Set<Object> tables = new HashSet<>();
//        tables.addAll(Arrays.asList("SYS.DBA_USERS"));
//        String format = "tabular";
//        String filename = "%i-%t-%d_%u.txt";
//        exporter.setTables(tables);
//        exporter.setFormat(format);
//        exporter.setFilename(filename);
//        exporter.setParallel(parallel);
//        exporter.execute();
//        verify(mockWriter, times(1)).write(exporter.getRset(), exporter.getFn());
    }

//    @Test
    public void exportFourTimes() throws SQLException {
//        WriterControl mockWriter = Mockito.mock(TabularWriterControl.class);
//
//        DefaultExporterControl mockExporter = beanFactory.getBean(DefaultExporterControl.class, mockWriter);

//        DefaultExporterControl mockExporter = Mockito.mock(TabularExporterControl.class, mockWriter);
//        
//        int parallel = 1;
//        Set<Object> tables = new HashSet<>();
//        tables.addAll(Arrays.asList("SYS.DBA_USERS"));
//        String format = "tabular";
//        String filename = "%i-%t-%d_%u.txt";
////
//        mockExporter.setTables(tables);
//        mockExporter.setFormat(format);
//        mockExporter.setFilename(filename);
//        mockExporter.setParallel(parallel);
//        
//        mockExporter.execute();
//        verify(mockWriter, times(1));
//        JDBCPoolManager jdbc = new JDBCPoolManager();
//        jdbc.configure("jdbc:oracle:thin:system/dfh5irj9@h3mih230.ced.h3g.it:1522:emrep", "system", "dfh5irj9");
//        Connection connection = jdbc.getConnection();
//        Statement stmnt = connection.createStatement();
//        ResultSet rs = stmnt.executeQuery("SELECT * FROM SYS.DBA_USERS");
//        Filename fn = new Filename("./" + filename, filename);
//        mockExporter.execute();
//        verify(mockWriter, times(1)).write(rs, fn);
    }
}
