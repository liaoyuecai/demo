package com.demo.core.config.jpa;


import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.internal.DefaultMergeEventListener;
import org.hibernate.event.spi.MergeContext;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.property.access.internal.PropertyAccessStrategyBackRefImpl;
import org.hibernate.type.Type;
import org.hibernate.type.TypeHelper;

/**
 * 监听update事件。如果update的值为空则不更新
 */
public class IgnoreNullEventListener extends DefaultMergeEventListener {


    public static final IgnoreNullEventListener INSTANCE = new IgnoreNullEventListener();


    @Override
    protected void copyValues(EntityPersister persister, Object entity, Object target, SessionImplementor source, MergeContext copyCache) {
        if (entity == target) {
            TypeHelper.replace(persister, entity, source, entity, copyCache);
        } else {
            Object[] original = persister.getValues(entity);
            Object[] targets = persister.getValues(target);
            Type[] types = persister.getPropertyTypes();
            Object[] copied = new Object[original.length];
            for (int i = 0; i < types.length; i++) {
                if (original[i] == null ||
                        original[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY ||
                        original[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN
                ) {
                    copied[i] = targets[i];
                } else {
                    copied[i] = types[i].replace(original[i], targets[i], source, target, copyCache);
                }
            }
            persister.setValues(target, copied);
        }
    }

}