package com.demo.data.source.jpa.config;

import com.demo.utils.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Slf4j
public class CustomerBaseRepositoryImpl<T> extends SimpleJpaRepository<T, Integer>
        implements CustomerBaseRepository<T> {

    private final EntityManager entityManager;
    private final Class<T> clazz;

    public CustomerBaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.clazz = entityInformation.getJavaType();
    }

    public CustomerBaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
        this.clazz = domainClass;
    }

    @Override
    public int deleteUpdateByIds(List<Integer> ids) {
        StringBuilder sb = new StringBuilder("update ")
                .append(getTableName()).append("set deleted = 1 where id in(");
        for (int i = 0, m = ids.size(); i < m; i++) {
            if (i == m - 1) {
                sb.append(ids.get(i));
            } else {
                sb.append(ids.get(i)).append(",");
            }
        }
        sb.append(")");
        return entityManager.createNativeQuery(sb.toString()).executeUpdate();
    }

    @Override
    public <R> List<R> customQuery(String sql, Class<R> clazz, List<Object> params) {
        return entityManager.createNativeQuery(sql, clazz).getResultList();
    }

    @Override
    public List<T> findNotDeleted() {
        StringBuilder sb = new StringBuilder("SELECT * ").
                append(" FROM ").append(getTableName()).append(" WHERE deleted = 0");
        return entityManager.createNativeQuery(sb.toString(), clazz).getResultList();
    }

    @Override
    public List<T> findNotDeletedAndStatus() {
        StringBuilder sb = new StringBuilder("SELECT * ").
                append(" FROM ").append(getTableName()).append(" WHERE status = 1 and deleted = 0");
        return entityManager.createNativeQuery(sb.toString(), clazz).getResultList();
    }


    String getTableName(){
        Table table = clazz.getAnnotation(Table.class);
        return table == null ? StringUtils.toUnderscoreCase(clazz.getSimpleName()) : table.name();
    }

}
