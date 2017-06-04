package com.fil.shauni.mainframe.spi;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.fil.shauni.Main;
import com.fil.shauni.Project;
import com.fil.shauni.command.Command;
import com.fil.shauni.command.CommandLineSupporter;
import com.fil.shauni.command.DatabaseConfiguration;
import com.fil.shauni.command.export.SpringExporter;
import com.fil.shauni.command.montbs.DefaultMonTbs;
import com.fil.shauni.command.support.worksplitter.DefaultWorkSplitter;
import com.fil.shauni.concurrency.pool.FixedThreadPoolManager;
import com.fil.shauni.concurrency.pool.ThreadPoolManager;
import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.io.PropertiesFileManager;
import com.fil.shauni.mainframe.ui.CommandLinePresentationControl;
import com.fil.shauni.util.Sysdate;
import java.util.Arrays;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import static java.util.stream.Collectors.*;
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

    private CommandLineSupporter cliSupporter;

    @Getter
    private static final Map<String, Class<? extends Command>> commands = new TreeMap<>();

    Map<String, String> project, buildNumber;

    static {
        addCommand(SpringExporter.class, "exp");
        addCommand(DefaultMonTbs.class, "montbs");
    }

    private static void addCommand(final Class<? extends Command> clazz, String name) {
        commands.put(name, clazz);
    }

    private Class<? extends Command> toTask(final String name) {
        return commands.get(name);
    }

    private void printBanner() {
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
        printBanner();
        // Initialize configuration.. NOT HERE.. FIXME
        try {
            Class.forName("com.fil.shauni.Project"); //FIXME, name of the class is hardcoded...
        } catch (ClassNotFoundException e) {
            throw new ShauniException(600, "Configuration file " + Project.MAIN_CFG_PATH + " cannot be loaded.");
        }

        if (args != null && args.length > 0) {
        } else {
            throw new ShauniException(600, "Arguments cannot be null or empty");
        }

        String cmd = args[0];
        cliSupporter = new CommandLineSupporter(args);

        String command = cliSupporter.getCommand();
        Integer cluster = cliSupporter.getValue("cluster", Integer.class);
        if (cluster == null) {
            cluster = 1;
        }
        Integer parallel = cliSupporter.getValue("parallel", Integer.class);
        if (parallel == null) {
            parallel = 1;
        }

        log.info("Command -->\n  {}\n", command);

        this.clazz = this.toTask(cmd);
        if (this.clazz == null) {
            throw new ShauniException(1, "Command '" + cmd + "' is unknown.\nTask has been aborted!");
        }
        // Exporter Format -- FIXME. Don't like this here...
        String ecmd = cmd;
        if (this.clazz.isAssignableFrom(SpringExporter.class)) {
            String format = cliSupporter.getValue("format", String.class);
            if (format == null) { // FIXME: not open closed..
                ecmd += "tab";
            } else {
                ecmd += format;
            }
        }

        final String fcommand = ecmd;

        try {
            List<String> urls = propertiesFileManager.readAll(DatabaseConfiguration.MULTIDB_CONN);
            int urlsSize = urls.size();
            int cores = Runtime.getRuntime().availableProcessors(); // FIXME, not here...
            int maxCluster = Math.min(urlsSize, cores);
            if (cluster > maxCluster) {
                cluster = maxCluster;
                String coresMex = urlsSize < cores ? "due to connections configuration" : "max cores available";
                log.info("Cluster parameter adjusted to {} ({})\n", cluster, coresMex);
            }
            Map<Integer, List<String>> workset = new DefaultWorkSplitter<>().splitWork(cluster, urls);
            if (workset == null || workset.isEmpty()) {
                throw new ShauniException(600, "Could not split the workload.");
            }

            @SuppressWarnings("unchecked")
            Future<Long>[] threads = new Future[cluster];
            ExecutorService pool = FixedThreadPoolManager.getInstance(cluster,
                    new BasicThreadFactory.Builder().namingPattern("thread-%d").daemon(true).build());

            for (int i = 0; i < cluster; i++) {
                final int thread = i;
                List<String> sset = workset.get(i);

                Command c = (Command) Main.beanFactory.getBean(fcommand, clazz);
                
                CommandConfiguration conf = Main.beanFactory
                        .getBean(CommandConfiguration.class, sset, parallel, thread);
                c.setConfiguration(conf);
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
                threads[i] = pool.submit(() -> {
                    log.info("Session {} started at {}\n", thread, Sysdate.now(Sysdate.TIMEONLY));
                    long et = c.execute();
                    log.info("\nSession {} finished at {}\nElapsed time: {} s", thread, Sysdate.now(Sysdate.TIMEONLY), et / 1e3);
                    return et;
                });
            }
            FixedThreadPoolManager.shutdownPool();
            Long[] results = new Long[cluster];
            for (int i = 0; i < results.length; i++) {
                try {
                    results[i] = threads[i].get();
                    if (threads[i].isDone()) {
                        log.debug("Thread is done.");
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    throw new ShauniException(600, e.getMessage());
                }
            }
            ThreadPoolManager.shutdownPool();
            LongSummaryStatistics stats = Arrays.stream(results).collect(summarizingLong(d -> d));
            log.info("\nSummary:\n -> [{}]\tcount: {}\n\t\tmax: {}\tmin: {}\tavg: {}\t(ms)",
                    cmd, stats.getCount(), stats.getMax(), stats.getMin(), stats.getAverage());
        } catch (NoSuchBeanDefinitionException b) {
            throw new ShauniException(600, b.getMessage());
        }
    }

    public void printHelp() {
        StringBuilder sb = new StringBuilder();
        jc.usage(sb);
        log.info("Printing help..\n{}Task terminated", sb.toString());
    }

    public void printFooter() {
        log.info("\nClosing {}..", project.get("name"));
    }
}
