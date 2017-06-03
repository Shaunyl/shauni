package com.fil.shauni.command;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Filippo
 */
public class CommandStatus {

    @Getter @Setter
    private State state = State.NULL;

    @Getter
    private int warnings, errors;

    public void gotWarn() {
        warnings++;
    }

    public void gotError() {
        errors++;
    }

    private enum State {
        NULL, COMPLETED, ABORTED
    }
}
