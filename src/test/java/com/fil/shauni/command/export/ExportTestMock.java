//package com.fil.shauni.command.export;
//
//import static com.fil.shauni.Main.beanFactory;
//import com.fil.shauni.command.DatabaseCommandControl;
//import com.fil.shauni.db.pool.JDBCPoolManager;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.util.ArrayList;
//import junit.framework.TestCase;
//import static junit.framework.TestCase.assertEquals;
//import static junit.framework.TestCase.assertNotNull;
//import static junit.framework.TestCase.assertTrue;
//import org.apache.commons.dbcp.BasicDataSource;
//import org.apache.commons.io.FileUtils;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import static org.mockito.Matchers.any;
//import org.mockito.Mock;
//import static org.mockito.Mockito.when;
//import org.mockito.runners.MockitoJUnitRunner;
//
////import static org.powermock.api.easymock.PowerMock.createMock;
///**
// *
// * @author Filippo Testino (filippo.testino@gmail.com)
// */
//@RunWith(MockitoJUnitRunner.class)
//public class ExportTestMock extends TestCase {
//
//    @Mock
//    private BasicDataSource ds;
//
//    @Mock
//    private Connection c;
//
//    @Mock
//    private Statement statement;
//
//    @Mock
//    private ResultSet resultSet;
//
//    @Mock
//    private JDBCPoolManager pool;
//
//    @Before @Override
//    public void setUp() throws Exception {
//        assertNotNull(ds);
//        when(c.createStatement()).thenReturn(statement);
//        when(ds.getConnection()).thenReturn(c);
//    }
//
//    @Test(timeout = 2000)
//    public void testExportTabularResultSetOneThreadOneParallel() throws Exception {
//        TabularExporter exporter = (TabularExporter) beanFactory.getBean("exptab", SpringExporter.class);
//
//        exporter.cluster = 1;
//        exporter.directory = "out";
//        exporter.filename = "%t-%d_[%w%u]";
//        exporter.parallel = 1;
//        exporter.format = "tab";
//        exporter.tables = new ArrayList<String>() {
//            private static final long serialVersionUID = 1L;
//            {
//                add("sys.dba_users");
//            }
//        };
//
//        exporter.setExporterExtractor((ResultSet rs) -> {
//            if (rs == null) { // ResultSet is always null here..
//                when(resultSet.first()).thenReturn(true);
//                when(resultSet.getInt(1)).thenReturn(29);
//                when(resultSet.getString(2)).thenReturn("Filippo");
//                when(resultSet.getString(3)).thenReturn("Testino");
//                when(statement.executeQuery(any(String.class))).thenReturn(resultSet);
//                when(resultSet.next()).thenReturn(true).thenReturn(false);
//            }
//            try {
//                exporter.setWriterManager(new MockWriter(new FileWriter(exporter.getFilename().getPath() + ".txt")));
//            } catch (IOException e) {
//                throw new RuntimeException(e.getMessage());
//            }
//            return new ExporterExtractor(exporter, exporter.getFilename()).extractData(resultSet);
//        });
//
//        DatabaseCommandControl cmd = ((DatabaseCommandControl) exporter);
//        cmd.setDataSource(ds);
////        pool.setDataSource(ds);
//        cmd.setPoolManager(pool);
//
//        cmd.setConnections(new String[]{"jdbc:oracle:thin:system/ciao@CHIARA:1521:XE"});
//        cmd.execute();
//
//        File file = new File(System.getProperty("user.dir") + "\\" + exporter.getFilename().getPath() + ".txt");
//        assertTrue(file.exists());
//        
//        String text = FileUtils.readFileToString(file).trim();
//        assertEquals(text, "29                        Filippo                   Testino                   ".trim());
//    }
//}
