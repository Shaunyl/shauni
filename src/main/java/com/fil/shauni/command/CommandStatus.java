package com.fil.shauni.command;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
public class CommandStatus {

    @Getter @Setter
    private State state = State.NULL;

    @Getter
    private int errors;
    
    public void error() {
        errors++;
    }

    public enum State {
        NULL, COMPLETED, ABORTED
    }
}
