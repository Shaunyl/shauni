package com.fil.shauni.db.spring;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 *
 * @author Filippo Testino (filippo.testino@gmail.com)
 */
@Configuration @ComponentScan(basePackages = { "com.fil.shauni" })
@ImportResource("file:src/main/resources/beans/Beans.xml")
@EnableTransactionManagement @PropertySource("classpath:/test/jdbc-derby-test.properties")
@EnableJpaRepositories(basePackages = { "com.fil.shauni.db.spring" })
public class TestConfig {

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

//    @Bean
//    public JdbcTemplate jdbcTemplate() {
//        return new JdbcTemplate(dataSource());
//    }

//    @Bean
//    public PlatformTransactionManager transactionManager() {
//        return new JpaTransactionManager(entityManagerFactory());
//    }

    private Map<String, Object> additionalProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
        return properties;
    }
}
