//package com.fil.shauni.command.export;
//
//import com.fil.shauni.command.export.support.ExporterObject;
//import com.fil.shauni.BeanConfiguration;
//import com.fil.shauni.command.support.WildcardContext;
//import com.fil.shauni.command.support.WorkSplitter;
//import com.fil.shauni.exception.ShauniException;
//import com.fil.shauni.util.Processor;
//import com.fil.shauni.util.file.Filepath;
//import java.util.ArrayList;
//import java.util.List;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.runners.MockitoJUnitRunner;
//
///**
// *
// * @author Filippo
// */
//@RunWith(MockitoJUnitRunner.class)
//public class SpringExporterTest {
//
//    private static SpringExporter exporter;
//
//    private static BeanConfiguration configuration;
//    
//    private static List<Processor<Filepath, WildcardContext>> wildcardReplacers;
//    
//    private static WorkSplitter<ExporterObject> workSplitter;
//
//    @BeforeClass
//    public static void setUp() {
//        configuration = new BeanConfiguration();
//        wildcardReplacers = configuration.wildcardReplacers();
//        workSplitter = configuration.workSplitter();
//    }
//
//    @Before
//    public void setUpTest() throws Exception {
//        exporter = new TabularExporter(wildcardReplacers, workSplitter);
////        exporter.setCurrentThreadName("thread-1");
//    }
//
//    @Test
//    public void testValidateQueriesAndTablesBothNull() throws ShauniException {
////        Check validate = exporter.validate();
////        Assert.assertEquals("(thread-1) At least one parameter bewteen -queries and -tables must be specified", validate.getMessage());
////        Assert.assertEquals(1040, validate.getCode());
//    }
//
//    @Test
//    public void testValidateQueriesAndTablesBothSet() throws ShauniException {
//        exporter.tables = new ArrayList<ExporterObject>() {
//            {
//                add(new ExporterTableObject("sys.dba_registry"));
//                add(new ExporterTableObject("sys.dba_users"));
//            }
//        };
//        exporter.queries = new ArrayList<ExporterObject>() {
//            {
//                add(new ExporterQueryObject("select * from sys.dba_registry"));
//                add(new ExporterQueryObject("select * from sys.dba_users"));
//            }
//        };
////        Check validate = exporter.validate();
////        Assert.assertEquals("(thread-1) Parameters -queries and -tables are mutually exclusive", validate.getMessage());
////        Assert.assertEquals(1041, validate.getCode());
//    }
//    
//    @Test
//    public void testValidateNegativeParallel() throws ShauniException {
//        exporter.parallel = -1;
//        exporter.tables = new ArrayList<ExporterObject>() {
//            {
//                add(new ExporterTableObject("sys.dba_registry"));
//                add(new ExporterTableObject("sys.dba_users"));
//            }
//        };
////        Check validate = exporter.validate();
////        Assert.assertEquals("(thread-1) Parallel degree must be greater than zero", validate.getMessage());
////        Assert.assertEquals(1042, validate.getCode());
//    }
//}
