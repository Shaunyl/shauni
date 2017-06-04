package com.fil.shauni.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;
import java.util.List;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Shaunyl
 */
@Log4j2
public abstract class Command {

    @Parameter(names = "--help", help = true, description = "Prints the help") @Getter
    protected boolean help = false;

    @Getter @Parameter(required = true, arity = 1)
    protected final List<String> cmd = Lists.newArrayList(1);

//    protected String name;

//    protected String description;

    protected CommandConfiguration configuration;
    
    protected CommandStatus status;

    public Command() {
        status = new CommandStatus();
    }
    
    public void setConfiguration(CommandConfiguration configuration) {
        this.configuration = configuration;
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
            throw new RuntimeException("Validation went wrong.");
        }

        setup();

        for (int s = 0; s < configuration.getSessions(); s++) {
            long et = 0;
            try {
                long st = System.currentTimeMillis();
                run(s);
                et = System.currentTimeMillis() - st;
            } finally {
                log.info("Task #{} of session {} finished in {} ms", s, configuration.getTid(), et / 1e3);
                takedown();
            }
        }

        log.debug("Session finished");
        return System.currentTimeMillis() - start;
    }

    public void run(int sid) {
        runWorker();
    }

    public void runWorker() {
        for (int i = 0; i < configuration.getParallel(); i++) {
            runJob(i);
        }
    }

    public abstract void runJob(int w);
}
