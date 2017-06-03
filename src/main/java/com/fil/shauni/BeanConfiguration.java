package com.fil.shauni;

import com.fil.shauni.command.export.SpringExporter;
import com.fil.shauni.command.support.worksplitter.DefaultWorkSplitter;
import com.fil.shauni.command.support.worksplitter.WorkSplitter;
import com.fil.shauni.db.pool.DatabasePoolManager;
import com.fil.shauni.db.pool.JDBCPoolManager;
import com.fil.shauni.util.Processor;
import com.fil.shauni.util.file.Filepath;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 *
 * @author Filippo
 */
@org.springframework.context.annotation.Configuration @ComponentScan(basePackages = { "com.fil.shauni" })
@ImportResource("file:src/main/resources/beans/Beans.xml")
public class BeanConfiguration {

    @Bean
    public <T> WorkSplitter<T> workSplitter() {
        return new DefaultWorkSplitter<>();
    }
    
    @Bean
    public List<Processor<Filepath, SpringExporter.WildcardContext>> wildcardReplacers() {
        return new ArrayList<Processor<Filepath, SpringExporter.WildcardContext>>() {
            {
                add((s, c) -> s.replaceWildcard("%w", Integer.toString(c.getWorkerId())));
                add((s, c) -> s.replaceWildcard("%u", Integer.toString(c.getObjectId())));
                add((s, c) -> s.replaceWildcard("%d", c.getTimestamp()));
                add((s, c) -> s.replaceWildcard("%n", c.getThreadName()));
                add((s, c) -> s.replaceWildcard("%t", c.getTable()));
            }
        };
    }
    
    @Bean
    public DatabasePoolManager databasePoolManager() {
        return new JDBCPoolManager();
    }
}
