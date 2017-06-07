package com.fil.shauni.command;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@Getter @AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandConfiguration {

    private int tid;

    private String tname;

    private int sessions;

    private List<String> workset;

    private boolean firstThread;

    public static CommandConfigurationBuilder builder() {
        return new CommandConfigurationBuilder();
    }

    @NoArgsConstructor
    public static class CommandConfigurationBuilder {

        private int tid;

        private String tname;

        private int sessions = 1;

        private List<String> workset;

        private boolean firstThread;

        public CommandConfigurationBuilder sessions(int sessions) {
            this.sessions = sessions;
            return this;
        }
        
        public CommandConfigurationBuilder tname(String tname) {
            this.tname = tname;
            return this;
        }
        
        public CommandConfigurationBuilder tid(int tid) {
            this.tid = tid;
            return this;
        }
        
        public CommandConfigurationBuilder workset(List<String> workset) {
            this.workset = workset;
            return this;
        }
        
        public CommandConfigurationBuilder firstThread(boolean firstThread) {
            this.firstThread = firstThread;
            return this;
        }

        public CommandConfiguration build() {
            return new CommandConfiguration(tid, tname, sessions, workset, firstThread);
        }
    }
}
