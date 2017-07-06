package com.fil.shauni.command.writer.spi.montbs.config;

import com.beust.jcommander.internal.Lists;
import com.fil.shauni.command.writer.WriterConfiguration;
import java.util.List;
import lombok.Getter;

/**
 *
 * @author Filippo
 */
@Getter
public class MontbsWriterConfiguration extends WriterConfiguration {

    private final String host, instance;

    private final int warning, critical;

    private final boolean undo, growing, auto, persist;

    private final char unit;

    private final List<String> exclude;

    public static class Builder extends WriterConfiguration.Builder<Builder> {

        private final String host, instance;

        private int warning = 85, critical = 95;

        private boolean undo = false, growing = false, auto = false, persist = false;

        private char unit = 'h';

        private List<String> exclude = Lists.newArrayList();

        public Builder(String host, String instance) {
            this.host = host;
            this.instance = instance;
        }
        
        public Builder warning(int warning) {
            this.warning = warning;
            return this;
        }

        public Builder critical(int critical) {
            this.critical = critical;
            return this;
        }
        
        public Builder undo(boolean undo) {
            this.undo = undo;
            return this;
        }
        
        public Builder auto(boolean auto) {
            this.auto = auto;
            return this;
        }
                
        public Builder growing(boolean growing) {
            this.growing = growing;
            return this;
        }
        
        public Builder persist(boolean persist) {
            this.persist = persist;
            return this;
        }
        
        public Builder unit(char unit) {
            this.unit = unit;
            return this;
        }
        
        public Builder exclude(List<String> exclude) {
            this.exclude = exclude;
            return this;
        }
        
        @Override
        public MontbsWriterConfiguration build() {
            return new MontbsWriterConfiguration(this);
        }
    }

    protected MontbsWriterConfiguration(Builder builder) {
        super(builder);
        this.host = builder.host;
        this.instance = builder.instance;
        this.warning = builder.warning;
        this.critical = builder.critical;
        this.undo = builder.undo;
        this.growing = builder.growing;
        this.unit = builder.unit;
        this.exclude = builder.exclude;
        this.auto = builder.auto;
        this.persist = builder.persist;
    }
}
