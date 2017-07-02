package com.fil.shauni;

import com.fil.shauni.command.CommandConfiguration;
import com.fil.shauni.command.export.SpringExporter;
import com.fil.shauni.command.support.worksplitter.DefaultWorkSplitter;
import com.fil.shauni.command.support.worksplitter.WorkSplitter;
import com.fil.shauni.db.pool.DatabasePoolManager;
import com.fil.shauni.db.pool.JDBCPoolManager;
import com.fil.shauni.util.Processor;
import com.fil.shauni.util.file.Filepath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@Configuration @ComponentScan(basePackages = {"com.fil.shauni"}) @Profile({ "production" })
@ImportResource("file:src/main/resources/beans/Beans.xml")
@EnableTransactionManagement @PropertySource("classpath:/jdbc-derby.properties")
@EnableJpaRepositories(basePackages = { "com.fil.shauni.db.spring.dao" })
public class BeanConfiguration {

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setPackagesToScan("com.fil.shauni");
        factory.setPersistenceUnitName("hibernate-punit");
        factory.setDataSource(dataSource());
        factory.setJpaPropertyMap(additionalProperties());
        factory.afterPropertiesSet();

        return factory.getObject();
    }
    
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
                add((s, c) -> s.replaceWildcard("%t", c.getTable().replace("$", "\\$").trim()));
            }
        };
    }

    @Bean
    public DatabasePoolManager databasePoolManager() {
        return new JDBCPoolManager();
    }

    @Bean
    @Scope("prototype")
    public CommandConfiguration commandConfiguration(List<String> sessions, int thread, boolean firstThread) {
        return new CommandConfiguration.CommandConfigurationBuilder()
                .sessions(sessions.size())
                .firstThread(firstThread)
                .tid(thread).tname("thread-" + thread)
                .workset(sessions)
                .build();
    }

    @Value("#{ environment['jdbc.url'] }")
    protected String databaseUrl;

    @Value("#{ environment['jdbc.username'] }")
    protected String databaseUserName = "";

    @Value("#{ environment['jdbc.password'] }")
    protected String databasePassword = "";

    @Value("#{ environment['database.driverClassName'] }")
    protected String driverClassName = "org.apache.derby.jdbc.EmbeddedDriver";

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(databaseUserName);
        dataSource.setPassword(databasePassword);
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);
        dataSource.setTestWhileIdle(true);
        dataSource.setTimeBetweenEvictionRunsMillis(1800000);
        dataSource.setNumTestsPerEvictionRun(3);
        dataSource.setMinEvictableIdleTimeMillis(1800000);
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
    
    private Map<String, Object> additionalProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
        return properties;
    }
}
