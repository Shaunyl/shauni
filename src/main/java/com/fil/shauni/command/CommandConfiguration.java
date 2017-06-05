package com.fil.shauni.command;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Filippo
 */
@Builder @Getter
public class CommandConfiguration {
    private int tid;
    private String tname;
    private int sessions = 1;
    private int parallel = 1;
    private List<String> workset;
    private boolean firstThread;
}
