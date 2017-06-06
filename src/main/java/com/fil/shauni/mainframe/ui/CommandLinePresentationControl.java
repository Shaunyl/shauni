package com.fil.shauni.mainframe.ui;

import java.util.List;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
public interface CommandLinePresentationControl {   
    void executeCommand(final List<String> args) throws Exception;
}
