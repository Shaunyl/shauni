package com.fil.shauni.mainframe.ui;

import java.util.List;

/**
 *
 * @author Shaunyl
 */
public interface CommandLinePresentationControl {   
    void executeCommand(final List<String> args) throws Exception;
}
