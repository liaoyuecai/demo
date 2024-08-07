package com.demo.core.config.jpa;

import com.demo.core.service.QueryCriteria;
import com.demo.core.utils.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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
    public int deleteUpdateByIds(Collection<Integer> ids) {
        StringBuilder sb = new StringBuilder("update ")
                .append(getTableName()).append(" set deleted = 1 where id in(?)");
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter(1, ids);
        return query.executeUpdate();
    }

    @Override
    public <R> List<R> customQuery(String sql, Class<R> clazz, List<Object> params) {
        Query query = entityManager.createNativeQuery(sql, clazz);
        if (params != null && !params.isEmpty()) {
            for (int i = 0, l = params.size(); i < l; i++)
                query.setParameter(i + 1, params.get(i));
        }
        return query.getResultList();
    }

    @Override
    public List<T> customQueryCriteria(String sql, List<QueryCriteria> criteria) {
        return this.customQueryCriteria(sql, clazz, criteria);
    }

    @Override
    public <R> List<R> customQueryCriteria(String sql, Class<R> clazz, List<QueryCriteria> criteria) {
        if (criteria != null && !criteria.isEmpty()) {
            StringBuilder sqlStr = new StringBuilder(sql);
            for (int i = 0, l = criteria.size(); i < l; i++) {
                if (i == 0) sqlStr.append(" where ");
                sqlStr.append(criteria.get(i).whereSql());
                if (i != l - 1) sqlStr.append(" and ");
            }
            Query query = entityManager.createNativeQuery(sqlStr.toString(), clazz);
            for (int i = 0, l = criteria.size(); i < l; i++) {
                query.setParameter(i + 1, criteria.get(i).getParameter());
            }
            return query.getResultList();
        }

        return entityManager.createNativeQuery(sql, clazz).getResultList();
    }

    @Override
    public Page<T> customQueryCriteriaPage(String sql, List<QueryCriteria> criteria, Pageable pageable) {
        return this.customQueryCriteriaPage(sql, clazz, criteria, pageable);
    }

    @Override
    public <R> Page<R> customQueryCriteriaPage(String sql, Class<R> clazz, List<QueryCriteria> criteria, Pageable pageable) {
        String querySql = sql;
        if (criteria != null && !criteria.isEmpty()) {
            StringBuilder sqlStr = new StringBuilder(sql);
            boolean where = false;
            for (int i = 0, l = criteria.size(); i < l; i++) {
                if (!where) {
                    sqlStr.append(" where ");
                    where = true;
                }
                if (criteria.get(i).getParameter() != null) {
                    sqlStr.append(criteria.get(i).whereSql());
                    if (i != l - 1) sqlStr.append(" and ");
                }
            }
            querySql = sqlStr.toString();
        }
        String countSql = "SELECT COUNT(0) " + querySql.toString().substring(querySql.toString().toLowerCase().indexOf("from"));
        querySql.equals(getSortStr(pageable));
        Query countQuery = entityManager.createNativeQuery(countSql);
        Query listQuery = entityManager.createNativeQuery(querySql.toString());
        if (criteria != null && !criteria.isEmpty()) {
            for (int i = 0, l = criteria.size(); i < l; i++) {
                if (criteria.get(i).getParameter() != null) {
                    countQuery.setParameter(i + 1, criteria.get(i).getParameter());
                    listQuery.setParameter(i + 1, criteria.get(i).getParameter());
                }
            }
        }
        listQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        listQuery.setMaxResults(pageable.getPageSize());
        return new PageImpl<>(listQuery.getResultList(), pageable, (long) countQuery.getSingleResult());
    }

    /**
     * 组合排序字段
     *
     * @param pageable
     * @return
     */
    private String getSortStr(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            StringBuilder sortBuilder = new StringBuilder(" ORDER BY ");
            sort.forEach(order -> {
                sortBuilder.append(StringUtils.toUnderscoreCase(order.getProperty())).append(" ");
                switch (order.getDirection()) {
                    case ASC -> sortBuilder.append("ASC");
                    case DESC -> sortBuilder.append("DESC");
                }
                sortBuilder.append(",");
            });
            String sortStr = sortBuilder.toString();
            return sortStr.substring(0, sortStr.length() - 1);
        }
        return "";
    }


    @Override
    public List<T> findNotDeleted() {
        StringBuilder sb = new StringBuilder("SELECT * FROM ").append(getTableName()).append(" WHERE deleted != 1");
        return entityManager.createNativeQuery(sb.toString(), clazz).getResultList();
    }

    @Override
    public List<T> findNotDeletedAndStatus() {
        StringBuilder sb = new StringBuilder("SELECT * FROM ").append(getTableName()).append(" WHERE status != 0 and deleted != 1");
        return entityManager.createNativeQuery(sb.toString(), clazz).getResultList();
    }

    @Override
    public void deleteByIds(Collection<Integer> ids) {
        StringBuilder sb = new StringBuilder("DELETE FROM ").append(getTableName()).append(" WHERE id in (?)");
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter(1, ids);
        query.executeUpdate();
    }


    String getTableName() {
        Table table = clazz.getAnnotation(Table.class);
        return table == null ? StringUtils.toUnderscoreCase(clazz.getSimpleName()) : table.name();
    }


}
