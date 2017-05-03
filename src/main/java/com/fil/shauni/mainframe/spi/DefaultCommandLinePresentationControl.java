package com.fil.shauni.mainframe.spi;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.fil.shauni.Main;
import com.fil.shauni.command.Command;
import com.fil.shauni.command.CommandLineSupporter;
import com.fil.shauni.command.DatabaseCommandControl;
import com.fil.shauni.command.config.DefaultCSAdder;
import com.fil.shauni.command.config.DefaultCSViewer;
import com.fil.shauni.command.export.DefaultExporter;
import com.fil.shauni.command.memory.DefaultMonMem;
import com.fil.shauni.command.montbs.DefaultMonTbs;
import com.fil.shauni.command.support.DefaultWorkSplitter;
import com.fil.shauni.command.support.WorkSplitter;
import com.fil.shauni.concurrency.pool.FixedThreadPoolManager;
import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.io.DBConfigurationFileManager;
import com.fil.shauni.io.PropertiesFileManager;
import com.fil.shauni.mainframe.ui.CommandLinePresentationControl;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javax.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Shaunyl
 */
@Component @Log4j2 @NoArgsConstructor
public class DefaultCommandLinePresentationControl implements CommandLinePresentationControl {

    private Class<? extends Command> clazz;

    private JCommander jc;

    @Inject
    private PropertiesFileManager propertiesFileManager;

    @Inject
    private DBConfigurationFileManager dbConfigurationFileManager;

    private CommandLineSupporter cliSupporter;

    @Getter
    private static final Map<String, Class<? extends Command>> commands = new TreeMap<>();
    
    Map<String, String> project, buildNumber;

    // List of commands goes here:
    static {
        addCommand(DefaultExporter.class, "exp");
        addCommand(DefaultMonTbs.class, "montbs");
        addCommand(DefaultMonMem.class, "monmem");

        // Configuration commands
        addCommand(DefaultCSAdder.class, "addcs");
        addCommand(DefaultCSViewer.class, "viewcs");

    }

    private static void addCommand(final Class<? extends Command> clazz, String name) {
        commands.put(name, clazz);
    }

    private Class<? extends Command> toTask(final String name) {
        return commands.get(name);
    }

    @Override
    public void printBanner() {
        project = propertiesFileManager.readAllWithKeys("target/classes/project.properties", "");
        String version = project.get("version");
        buildNumber = propertiesFileManager.readAllWithKeys("buildNumber.properties", "");
        version += "." + buildNumber.get("buildNumber") + buildNumber.get("status");
        String banner = project.get("name") + " " + version + " - Built at " + project.get("build.date") + "\n";
        banner += "Copyright (c) " + buildNumber.get("dates") + ", " + buildNumber.get("author") + ". All rights reserved.";

        log.info("{}\n", banner);
    }

    @Override
    public void executeCommand(final String args[]) throws Exception {

        if (args != null && args.length > 0) {
        } else {
            throw new ShauniException(600, "Arguments cannot be null or empty");
        }

        String cmd = args[0];
        cliSupporter = new CommandLineSupporter(args);

        String command = cliSupporter.getCommand();
        int cluster = cliSupporter.getValue("cluster", Integer.class);

        log.info("Command -->\n  {}\n", command);

        this.clazz = this.toTask(cmd);
        if (this.clazz == null) {
            throw new ShauniException(1, "Command '" + cmd + "' is unknown.\nTask has been aborted!");
        }

        // Exporter Format -- FIXME. Don't like this here...
        String ecmd = cmd;
        if (this.clazz.isAssignableFrom(DefaultExporter.class)) {
            ecmd += cliSupporter.getValue("format", String.class);
            if (ecmd.equals("exp")) {
                ecmd += "tab";
            }
        }

        List<String> urls = null;
        boolean isDBCommand = false;
        if (this.clazz.getSuperclass() == DatabaseCommandControl.class) {
            isDBCommand = true;
            try {
                urls = dbConfigurationFileManager.read();
                if (urls == null) {
                    throw new ShauniException(1014, "No connections available.\n  Tip: use addcs command to add a new connection string");
                }

            } catch (ShauniException e) {
//                throw new RuntimeException("Internal Error: " + e.getMessage());
            }
        }

        final String fcommand = ecmd;

        try {
            Future<Long>[] threads = new Future[cluster];
            ExecutorService pool = FixedThreadPoolManager.getInstance(cluster
                ,  new BasicThreadFactory.Builder().namingPattern("thread-%d").daemon(true).build());

            WorkSplitter wp = new DefaultWorkSplitter();
            Map<Integer, String[]> splitWork = wp.splitWork(cluster, urls);
            for (int node = 0; node < cluster; node++) {
                final Command c = Main.beanFactory.getBean(fcommand, clazz);
                if (isDBCommand) {
                    DatabaseCommandControl dcc = ((DatabaseCommandControl) c);
                    // Set a bunch of connections to this thread.
                    dcc.setConnections(splitWork.get(node));
                }
                try {
                    jc = new JCommander(c);
                    jc.parse(args);
                    
                    int ndparams = cliSupporter.countNoDashedParameters();
                    if (ndparams > 0) {
                        throw new ParameterException("Options without a dash in front are not supported");
                    }
                    
                    int mparams = c.getCmd().size();
                    if (mparams > 1) {
                        throw new ParameterException("Main parameters cannot be more than one");
                    }
        
                } catch (ParameterException pe) {
                    throw new ShauniException(600, pe.getMessage());
                }
                
                if (c.isHelp()) {
                    printHelp();
                    break; // for help no need to invoke different threads..
                }
                if (c.isCluster()) { // FIXME
                    threads[node] = pool.submit(c);
                } else {
                    c.execute();
                }
            }
            FixedThreadPoolManager.shutdownPool();
            for (Future<Long> thread : threads) {
                try {
                    thread.get();
                    if (thread.isDone()) {
                        log.info("Thread is done.");
                    }
                } catch (ExecutionException | InterruptedException e) {
                    throw new ShauniException(600, e.getMessage());
                }
            }
        } catch (NoSuchBeanDefinitionException b) {
            throw new ShauniException(600, b.getMessage());
        }
        System.exit(0);
    }

    public void printHelp() {
        StringBuilder sb = new StringBuilder();
        jc.usage(sb);
        log.info("Printing help..\n");
        log.info(sb);
        log.info("Task terminated");
    }

    @Override
    public void printFooter() {
        log.info("\nClosing {}..", project.get("name"));
    }
}
