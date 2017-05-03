package com.fil.shauni.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;
import com.fil.shauni.command.support.CommandStatus;
import com.fil.shauni.exception.ShauniException;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Shaunyl
 */
@NoArgsConstructor
public abstract class Command implements Callable<Long> {
    
    @Getter
    protected boolean isCluster = false;
    
    protected int errorCount = 0;
    
    @Setter
    protected PrintStream stream;
    
    @Parameter(names = "--help", help = true, description = "Prints the help") @Getter
    protected boolean help = false;
    
    @Getter @Parameter(required = true, arity = 1)
    protected final List<String> cmd = Lists.newArrayList(1);

    @Getter
    protected CommandStatus status;

    @Getter
    protected String name;

    @Getter
    private String description;

    /**
     * Add a description to the command
     * @param description the command's description
     * @return the command itself
     */
    public Command withDescription(String description) {
        this.status = new CommandStatus();
        this.description = description;
        return this;
    }

    /**
     * Setup the command
     * @throws com.fil.shauni.exception.ShauniException
     */
    public void setup() throws ShauniException {
    }
    
    /**
     * Takedown the command
     */
    public void takedown() {
    }
    
    
    /**
     * Validate the syntax and the options of the command
     * @throws com.fil.shauni.exception.ShauniException 
     */
    public void validate() throws ShauniException {}
    
    /**
     * Execute the command
     * @return elapsed time (s)
     */
    public abstract long execute();
    
    public long execute(int node) { return 0; }

    public void run() throws ShauniException {}
}
