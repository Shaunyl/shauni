package com.fil.shauni.command.writer;

import java.io.Writer;
import lombok.Getter;

/**
 *
 * @author Filippo
 */
@Getter
public class WriterConfiguration {

    protected final Writer writer;

    public static class Builder<T extends Builder> {

        private Writer writer = null;

        public Builder() {}

        public T writer(Writer writer) {
            this.writer = writer;
            return (T) this;
        }

        public WriterConfiguration build() { return new WriterConfiguration(this); }
    }

    protected WriterConfiguration(Builder builder) {
        writer = builder.writer;
    }
}
