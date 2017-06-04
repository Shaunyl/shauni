package com.fil.shauni.mainframe.spi;

import com.fil.shauni.command.parser.CommandParser;
import com.fil.shauni.command.parser.spi.SpringExporterParser;
import com.fil.shauni.command.CommandConfiguration;
import com.beust.jcommander.*;
import com.fil.shauni.Main;
import com.fil.shauni.command.*;
import com.fil.shauni.command.export.SpringExporter;
import com.fil.shauni.command.montbs.DefaultMonTbs;
import com.fil.shauni.command.support.worksplitter.DefaultWorkSplitter;
import com.fil.shauni.concurrency.pool.*;
import com.fil.shauni.exception.ShauniException;
import com.fil.shauni.io.spi.PropertiesFileManager;
import com.fil.shauni.mainframe.ui.CommandLinePresentationControl;
import com.fil.shauni.util.Sysdate;
import java.util.*;
import static com.fil.shauni.util.GeneralUtil.*;
import java.util.concurrent.*;
import static java.util.stream.Collectors.*;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Filippo Testino
 */
@Component @Log4j2 @NoArgsConstructor
public class DefaultCommandLinePresentationControl implements CommandLinePresentationControl {

    private Class<? extends Command> clazz;

    private JCommander jc;

    @Inject
    private PropertiesFileManager propertiesFileManager;

    @Getter
    private static final Map<String, Class<? extends Command>> commands = new HashMap<>();

    private static final Map<Class<? extends Command>, Class<? extends CommandParser>> parsers = new HashMap();

    private Map<String, String> project, buildNumber;

    static {
        addCommand("exp", SpringExporter.class, SpringExporterParser.class);
        addCommand("montbs", DefaultMonTbs.class, null);
    }

    @PostConstruct
    public void init() {
        printBanner();
    }

    private static void addCommand(String name, final Class<? extends Command> c, Class<? extends CommandParser> p) {
        commands.put(name, c);
        parsers.put(c, p);
    }

    private Class<? extends Command> nameToClass(final String name) {
        return commands.get(name);
    }

    @Override @SuppressWarnings("unchecked")
    public void executeCommand(final String args[]) throws Exception {
        if (args == null || args.length == 0) {
            throw new ShauniException(600, "Arguments cannot be null or empty");
        }
        String cmd = args[0];

        CommandLineSupporter cliSupporter = new CommandLineSupporter(args);

        String command = cliSupporter.getCommand();
        Integer cluster = cliSupporter.getValue("cluster", Integer.class, 1);
        Integer parallel = cliSupporter.getValue("parallel", Integer.class, 1);

        log.info("Command -->\n  {}\n", command);

        this.clazz = this.nameToClass(cmd);
        if (this.clazz == null) {
            throw new ShauniException(1, "Command '" + cmd + "' is unknown.\nTask has been aborted!");
        }

        Class<? extends CommandParser> parser = parsers.get(clazz);
        if (parser != null) {
            clazz = (Class<? extends Command>) parser.getDeclaredMethod("parse", String[].class).invoke(parser.newInstance(), cliSupporter, new Object[]{ args });
        }

        try {
            Map<Integer, List<String>> workset;
            if (clazz.isAssignableFrom(DatabaseCommandControl.class)) {
                List<String> urls = propertiesFileManager.readAll(DatabaseConfiguration.MULTIDB_CONN);
                int urlsSize = urls.size();
                int cores = availableProcessors();
                int maxCluster = Math.min(urlsSize, cores);
                if (cluster > maxCluster) {
                    cluster = maxCluster;
                    String coresMex = urlsSize < cores ? "based on configuration" : "max cores available";
                    log.info("Cluster parameter adjusted to {} ({})\n", cluster, coresMex);
                }
                workset = new DefaultWorkSplitter<>().splitWork(cluster, urls);
            } else {
                throw new ShauniException(600, "Command not supported.");
            }
            
            if (workset == null || workset.isEmpty()) {
                throw new ShauniException(600, "Could not split the workload.");
            }

            Future<Long>[] threads = new Future[cluster];
            ExecutorService pool = FixedThreadPoolManager.getInstance(cluster, new BasicThreadFactory.Builder().namingPattern("thread-%d").daemon(true).build());

            for (int i = 0; i < cluster; i++) {
                final int thread = i;
                Command c = (Command) Main.beanFactory.getBean(clazz);
                CommandConfiguration conf = Main.beanFactory.getBean(CommandConfiguration.class, workset.get(i), parallel, thread);
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
            throw new ShauniException(600, "Command not found.\n" + b.getMessage());
        } finally {
            printFooter();
        }
    }

    private void printHelp() {
        StringBuilder sb = new StringBuilder();
        jc.usage(sb);
        log.info("Printing help..\n{}Task terminated", sb.toString());
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

    private void printFooter() {
        log.info("\nClosing {}..", project.get("name"));
    }
}
