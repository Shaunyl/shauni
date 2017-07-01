//package com.fil.shauni.db.spring.deprecated;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.PersistenceUnit;
//import lombok.extern.log4j.Log4j2;
//
///**
// *
// * @author Filippo
// */
//@Log4j2
//public abstract class SpringGenericDao<E, K> implements GenericDao<E, K>{
//
//    @PersistenceUnit
//    protected EntityManagerFactory entityManagerFactory;
//
//    protected EntityManager entityManager;
//    
//    @PostConstruct
//    public void init() {
//        log.debug("initializing Entity Manager..");
//        entityManager = entityManagerFactory.createEntityManager();
//    }
//    
//    @PreDestroy
//    public void destroy() {
//        log.debug("destroying Entity Manager..");
//        entityManager.close();
//        entityManagerFactory.close();
//    }
//}
