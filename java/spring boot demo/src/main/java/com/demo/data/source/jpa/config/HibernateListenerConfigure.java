package com.demo.data.source.jpa.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.context.annotation.Configuration;

/**
 * 覆盖Hibernate监听器。引入重写的监听器
 */
@Configuration
public class HibernateListenerConfigure {


    @PersistenceUnit
    private EntityManagerFactory emf;

    @PostConstruct
    protected void init() {
        SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.MERGE).clearListeners();
        registry.getEventListenerGroup(EventType.MERGE).prependListener(IgnoreNullEventListener.INSTANCE);
    }
}