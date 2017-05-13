package com.fil.shauni.command.support;

import java.io.PrintStream;

/**
 *
 * @author Shaunyl
 */
public class CommandStatus {
    public void print(PrintStream stream, final String msg, final Object... parameters) {
        stream.print(String.format(msg, parameters));
    }
    
    public void println(PrintStream stream, final String msg, final Object... parameters) {
        this.print(stream, msg + "%n", parameters);
    }
}
