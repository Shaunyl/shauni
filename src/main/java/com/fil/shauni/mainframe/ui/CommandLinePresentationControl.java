package com.fil.shauni.mainframe.ui;

/**
 *
 * @author Shaunyl
 */
public interface CommandLinePresentationControl {
    void printBanner();
    
    void executeCommand(String args[]) throws Exception;

    void printFooter();
}
