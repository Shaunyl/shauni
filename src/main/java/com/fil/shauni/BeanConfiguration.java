package com.fil.shauni;

import com.fil.shauni.command.export.support.DWildcardReplacer;
import com.fil.shauni.command.export.support.NWildcardReplacer;
import com.fil.shauni.command.export.support.UWildcardReplacer;
import com.fil.shauni.command.export.support.WWildcardReplacer;
import com.fil.shauni.command.export.support.WildcardReplacer;
import com.fil.shauni.command.support.DefaultWorkSplitter;
import com.fil.shauni.command.support.WorkSplitter;
import java.util.HashSet;
import java.util.Set;
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
    public Set<WildcardReplacer> wildcardReplacers() {
        return new HashSet<WildcardReplacer>() {
            {
                add(new WWildcardReplacer());
                add(new UWildcardReplacer());
                add(new DWildcardReplacer());
                add(new NWildcardReplacer());
            }
        };
    }
}
