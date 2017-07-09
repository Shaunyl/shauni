package com.fil.shauni.mainframe.spi;

import com.fil.shauni.command.parser.CommandParser;
import com.fil.shauni.command.parser.spi.SpringExporterParser;
import com.fil.shauni.command.CommandConfiguration;
import com.beust.jcommander.*;
import com.fil.shauni.Main;
import com.fil.shauni.ShauniCache;
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
import static com.fil.shauni.command.Command.CommandAction;
import static com.fil.shauni.command.CommandLineSupporter.*;
import com.fil.shauni.command.parser.spi.DefaultMonTbsParser;
import com.fil.shauni.db.spring.model.MontbsRun;
import com.fil.shauni.db.spring.service.MontbsRunService;
import com.fil.shauni.io.spi.ParFileManager;
import java.io.IOException;
import java.io.InputStream;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@Component @Log4j2 @NoArgsConstructor
public class DefaultCommandLinePresentationControl implements CommandLinePresentationControl {

    private JCommander jc;

    @Inject
    private PropertiesFileManager propertiesFileManager;

    @Inject
    private MontbsRunService montbsService;

    @Getter
    private static final Map<String, Command> COMMANDS = new HashMap<String, Command>();

    static {
        addCommand(new Command(SpringExporter.class, "exp", SpringExporterParser.class)
                .withDescription("Export data from tables or result set to dumpfiles in different formats"));
        addCommand(new Command(DefaultMonTbs.class, "montbs", DefaultMonTbsParser.class)
                .withDescription("Check tablespace usage and create a detailed report"));
    }

    private static void addCommand(final Command command) {
        COMMANDS.put(command.getName(), command);
    }

    @PostConstruct
    public void init() {
        printBanner();
    }

    protected Class<? extends CommandAction> toClass(final String name) {
        Command cmd = COMMANDS.get(name);

        if (cmd == null && !name.matches("help|version")) {
            log.info("Command '{}' not supported.", name);
            throw new ShauniException("Aborting..");
        }
        return cmd.getCmdClass();
    }

    @Override @SuppressWarnings("unchecked")
    public void executeCommand(List<String> args) throws Exception {
        if (args.isEmpty()) {
            throw new ShauniException(600, "No arguments provided.");
        }

        args = this.integrateParfile(args);
        String cmd = getCommandName(args);

        if (cmd.equals("help")) {
            printCliHelp(COMMANDS);
            return;
        }
        if (cmd.equals("version")) {
//            printVersion();
            return;
        }

//        String command = getCommand(args);
        Integer cluster = getValue(args, "cluster", Integer.class, 1);

//        log.info("Command -->\n  {}\n", command);
        Class<? extends CommandAction> clazz = this.toClass(cmd);
        if (clazz == null) {
            throw new ShauniException(1, "Command '" + cmd + "' is unknown.\nTask has been aborted!");
        }

        Class<? extends CommandParser> parser = COMMANDS.get(cmd).getParser();
        if (parser != null) {
            clazz = (Class<? extends CommandAction>) parser
                    .getDeclaredMethod("parse", List.class)
                    .invoke(parser.newInstance(), new Object[]{ args });
        }

        try {
            Map<Integer, List<String>> workset;
//            if (clazz.isAssignableFrom(DatabaseCommandControl.class)) { FIXME
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
//            } else {
//                throw new ShauniException(600, "Command not supported.");
//            }

            if (workset == null || workset.isEmpty()) {
                throw new ShauniException(600, "Could not split the workload.");
            }

            Future<Long>[] threads = new Future[cluster];
            ExecutorService pool = FixedThreadPoolManager.getInstance(cluster, new BasicThreadFactory.Builder().namingPattern("thread-%d").daemon(true).build());

            String command = getCommand(args);
            log.info("Command -->\n  {}\n", command);
            for (int i = 0; i < cluster; i++) {
                final int thread = i;
                CommandAction c = (CommandAction) Main.beanFactory.getBean(clazz);
                CommandConfiguration conf = Main.beanFactory.getBean(CommandConfiguration.class, workset.get(i), thread, i == 0);
                c.setConfiguration(conf); // FIXME: parallel is not a global parameter, shouldn't be here..
//                log.info("Thread {}, workset {}", thread, workset.get(i));
                try {
                    jc = new JCommander(c);
                    integrate(args);
                    jc.parse(args.toArray(new String[args.size()]));

                    checkForNoDashParameters(args);
                    int mparams = c.getCmd().size();
                    if (mparams > 1) {
                        throw new ParameterException("Main parameters cannot be more than one");
                    }

                } catch (ParameterException pe) {
                    throw new ShauniException(600, pe.getMessage());
                }

                if (c.isHelp()) {
                    printHelp();
                    return; // for help no need to invoke different threads..
                }
                threads[i] = pool.submit(() -> {
                    log.info("Session {} started at {}\n", thread, Sysdate.now(Sysdate.TIMEONLY));
                    long et = c.execute();
                    final CommandStatus.State s = c.getStatus().getState();
                    log.info("\nSession {} finished at {}\nElapsed time: {} s", thread,
                            Sysdate.now(Sysdate.TIMEONLY), et / 1e3);
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
//                    e.printStackTrace();
                    throw new ShauniException(600, e.getMessage());
                }
            }
            ThreadPoolManager.shutdownPool();

            /*
                FLUSH THE CACHE: FIXME.. not here.. temporary place..
             */
            Cache cache = ShauniCache.getInstance().getCache("dbcache");
            if (cache != null) {
                for (Object key : cache.getKeys()) {
                    Element element = cache.get(key);
                    if (element != null) {
                        montbsService.persist((MontbsRun) element.getObjectValue());
                    }
                }
                ShauniCache.shutdown();
            }
//            if (ca != null && ca.getStatus().getState() == CommandStatus.State.COMPLETED) {
            LongSummaryStatistics stats = Arrays.stream(results).collect(summarizingLong(d -> d));
            log.info("\nSummary:\n -> [{}]\tcount: {}\n\t\tmax: {}\tmin: {}\tavg: {}\t(ms)",
                    cmd, stats.getCount(), stats.getMax(), stats.getMin(), stats.getAverage());
//            }
        } catch (NoSuchBeanDefinitionException b) {
            throw new ShauniException(600, "Command not found.\n" + b.getMessage());
        }
    }

    private void printHelp() {
        StringBuilder sb = new StringBuilder();
        jc.usage(sb);
        log.info("{}", sb.toString());
    }

    private void printBanner() {

        String VERSION_FILE = "/version.properties";
        InputStream resourceAsStream = this.getClass().getResourceAsStream(VERSION_FILE);
        Properties prop = new Properties();
        try {
            prop.load(resourceAsStream);
        } catch (IOException e) {
//            log.error(VERSION_FILE + "not found!");
            throw new ShauniException(VERSION_FILE + "not found!");
        }

        StringBuilder buffer = new StringBuilder(prop.getProperty("name"));
        buffer.append(" Version ").append(prop.getProperty("version"))
                .append("-").append(prop.getProperty("buildNumber"))
                .append(", ").append(prop.getProperty("buildTimestamp"));
        log.info("{}\n", buffer);
    }

    private List<String> loadParfile(final String filename) {
        return new ParFileManager().readAll(filename);
    }

    private List<String> integrateParfile(List<String> args) {
        String parfile = getValue(args, "parfile", String.class, null);
        if (parfile == null) {
            return args;
        }

        List<String> params = this.loadParfile(parfile);
        return new ArrayList<String>() {
            {
                addAll(args);
                addAll(params);
            }
        };
    }
}
