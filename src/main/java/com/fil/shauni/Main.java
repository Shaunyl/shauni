package com.fil.shauni;

import com.fil.shauni.mainframe.ui.CommandLinePresentationControl;
import java.util.Locale;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

// addcs -key=url4 -user=system -password=dfh5irj9 -host=h3mih230.ced.h3g.it -port=1522 -sid=emrep
// exp -format=tab -tables=sys.dba_registry, sys.dba_users -parallel=2
/**
 *
 * @author Shaunyl
 */
@Log4j2
public class Main {

    public static final BeanFactory beanFactory;

    static {
        beanFactory = new ClassPathXmlApplicationContext("file:src/main/resources/beans/Beans.xml");
//        beanFactory = new AnnotationConfigApplicationContext(CommandLine.class);
    }

    public static void main(String[] args) {
        
        
        

        Locale.setDefault(new Locale("en"));
//        StoreKey sk = new StoreKey(); // key has already been initialized.
//        SecretKey key = sk.getKey();
//
//        File file = new File("config/multidb.cry");
//        try (CipherOutputStream cos = sk.append(file, key)) {
//            cos.write("url1=jdbc:oracle:thin:system/temp123@10.200.12.120:1528:ERMTS1\n".getBytes("UTF-8"));
//            cos.write("url2=jdbc:oracle:thin:system/dfh5irj9@h3mih230.ced.h3g.it:1522:emrep\n".getBytes("UTF-8"));
//        }
//
//        try (
//            CipherInputStream cis = sk.decrypt(file, key)) {
//            BufferedReader bread = new BufferedReader(new InputStreamReader(cis, "UTF-8"));
//
//            String line;
//            while ((line = bread.readLine()) != null) {
//                System.out.println(line);
//            }
//        }
        CommandLinePresentationControl cliControl = beanFactory.getBean(CommandLinePresentationControl.class);
        try {
            cliControl.printBanner();
            cliControl.executeCommand(args);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            cliControl.printFooter();
//            throw new ShauniException(e.getCode(), e.getMessage());
        }
    }

}

//        int parallel = 1;
//        Set<Object> tables = new HashSet<>();
//        tables.add("SELECT username, created FROM sys.dba_users");
//        tables.add("SYSTEM.REPCAT$_REPCAT");
//        tables.add("SYS.DBA_USERS");
//        tables.add("SYS.V_$INSTANCE");
//        String format = "tabular";
//        String filename = "%i%t-%d.txt";
//        Map<String, Integer> colformats = new HashMap<>();
//        colformats.put("username", 120);
//
//        DefaultExporterControl cmd = beanFactory.getBean(TabularExporterControl.class);
//        cmd.setParallel(parallel);
//        cmd.setQueries(tables);
//        cmd.setFormat(format);
//        cmd.setFilename(filename);
//        ((TabularExporterControl) cmd).setColformats(colformats);
//
//        cmd.execute();
