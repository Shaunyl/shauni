package com.fil.shauni.db.spring;

import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@Configuration @ComponentScan(basePackages = { "com.fil.shauni" })
@ImportResource("file:src/main/resources/beans/Beans.xml")
@EnableTransactionManagement @PropertySource("classpath:/test/jdbc-derby-test.properties")
public class TestConfig {

    @Value("#{ environment['jdbc.url'] }")
    protected String databaseUrl;

    @Value("#{ environment['jdbc.username'] }")
    protected String databaseUserName = "";

    @Value("#{ environment['jdbc.password'] }")
    protected String databasePassword = "";

    @Value("#{ environment['database.driverClassName'] }")
    protected String driverClassName;

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
}
