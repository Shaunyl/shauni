package com.fil.shauni.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.validators.PositiveInteger;
import com.fil.shauni.command.parser.CommandParser;
import com.fil.shauni.exception.ShauniException;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@Log4j2 @Getter
public class Command {

    @NonNull
    private final Class<? extends CommandAction> cmdClass;

    @NonNull
    private final String name;

    private String description;

    private Class<? extends CommandParser> parser;

    public Command(final @NonNull Class<? extends CommandAction> cmdClass,
            final @NonNull String name) {
        this(cmdClass, name, null);
    }

    public Command(final @NonNull Class<? extends CommandAction> cmdClass, final @NonNull String name,
            final Class<? extends CommandParser> parser) {
        this.name = name;
        this.cmdClass = cmdClass;
        this.parser = parser;
    }

    public Command withDescription(final @NonNull String description) {
        this.description = description;
        return this;
    }

    public static abstract class CommandAction {

        @Parameter(names = { "--help", "-h" }, help = true, description = "Prints this message") @Getter
        protected boolean help = false;

        @Parameter(names = { "-parfile" }, description = "Specifies the name of an export parameter file") @Getter
        protected String parfile = "shauni.par";

        @Parameter(names = "-cluster", arity = 1, validateWith = PositiveInteger.class, description = "Specify the number of concurrent threads")
        protected Integer cluster = 1;

        @Getter @Parameter(required = true, arity = 1)
        protected final List<String> cmd = Lists.newArrayList(1);

        protected CommandConfiguration configuration;

        @Getter
        protected CommandStatus status;

        protected boolean firstThread;

        public CommandAction() {
            this.status = new CommandStatus();
        }

        public void setConfiguration(final @NonNull CommandConfiguration configuration) {
            this.configuration = configuration;
            this.firstThread = configuration.getTid() == 0;
        }

        public boolean validate() {
            return true;
        }

        public void setup() {
        }

        public void takedown() {
        }

        public long execute() {
            log.debug("Session started");
            long start = System.currentTimeMillis();
            if (!validate()) {
                status.setState(CommandStatus.State.ABORTED);
                return 0;
            }

            setup();

            for (int s = 0; s < configuration.getSessions(); s++) {
                long et = 0;
                try {
                    long st = System.currentTimeMillis();
                    this.run(s);
                    et = System.currentTimeMillis() - st;
                } finally {
                    log.info("Task #{} of session {} finished in {} ms", s, configuration.getTid(), et / 1e3);
                    takedown();
                }
            }

            log.debug("Session finished");
            return System.currentTimeMillis() - start;
        }

        public abstract void run(int sid);

        public void runWorker(int parallel) {
            for (int i = 0; i < parallel; i++) {
                try {
                    runJob(i);
                } catch (Exception e) {
                    log.error("  -> Output file not created due to errors: \n{}", e.getMessage());
                }
            }
        }

        public abstract void runJob(int w) throws Exception;
    }
}
