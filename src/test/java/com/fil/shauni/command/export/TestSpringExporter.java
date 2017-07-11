//package com.fil.shauni.command.export;
//
//import com.fil.shauni.command.CommandConfiguration;
//import com.fil.shauni.command.export.spi.TabularExporter;
//import com.fil.shauni.command.export.support.Entity;
//import com.fil.shauni.command.export.support.Table;
//import com.fil.shauni.command.support.worksplitter.DefaultWorkSplitter;
//import com.fil.shauni.command.support.worksplitter.WorkSplitter;
//import com.fil.shauni.db.pool.DatabasePoolManager;
//import com.fil.shauni.db.pool.JDBCPoolManager;
//import com.fil.shauni.util.Processor;
//import com.fil.shauni.util.file.Filepath;
//import java.util.ArrayList;
//import java.util.List;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
///**
// *
// * @author Filippo
// */
//public class TestSpringExporter {
//
//    private static BeanFactory beanFactory;
//
//    private static ArrayList<Processor<Filepath, SpringExporter.WildcardContext>> wildcards;
//
//    private static WorkSplitter<String> workSplitter;
//
//    @BeforeClass
//    public static void setUp() { // should be removed..
//        beanFactory = new ClassPathXmlApplicationContext("file:src/main/resources/beans/Beans.xml");
//        wildcards = new ArrayList<Processor<Filepath, SpringExporter.WildcardContext>>() {
//            {
//                add((s, c) -> s.replaceWildcard("%w", Integer.toString(c.getWorkerId())));
//                add((s, c) -> s.replaceWildcard("%u", Integer.toString(c.getObjectId())));
//                add((s, c) -> s.replaceWildcard("%d", c.getTimestamp()));
//                add((s, c) -> s.replaceWildcard("%n", c.getThreadName()));
//                add((s, c) -> s.replaceWildcard("%t", c.getTable().replace("$", "\\$").trim()));
//            }
//        };
//        workSplitter = new DefaultWorkSplitter<>();
//    }
//
//    @Test
//    public void run() {
//        TabularExporter exporter = new TabularExporter(wildcards, workSplitter);
//
//        List<String> sessions = new ArrayList<>();
//        sessions.add("url4=jdbc:oracle:thin:system/ciao@FILIPPO-PC:1521:XE");
//
//        CommandConfiguration conf = new CommandConfiguration.CommandConfigurationBuilder()
//                .sessions(1)
//                .firstThread(true)
//                .tid(0).tname("thread-" + 0)
//                .workset(sessions)
//                .build();
//
//        exporter.setConfiguration(conf);
//
//        List<Entity> tables = new ArrayList<>();
//        tables.add(new Table("SYS.DBA_USERS"));
//        ExportMode mode = exporter.getMode();
//        mode.setTables(tables);
//        exporter.setDirectory("test");
//        DatabasePoolManager pool = new JDBCPoolManager();
//        pool.configure("jdbc:oracle:thin:system/ciao@FILIPPO-PC:1521:XE", "system", "ciao", "FILIPPO-PC", "XE");
//        exporter.setDatabasePoolManager(pool);
//
//        exporter.execute();
//
//        //FIXME 
//        /*
//        @Inject
//        private PropertiesFileManager propertiesFileManager;
//        
//         */
//    }
//}
