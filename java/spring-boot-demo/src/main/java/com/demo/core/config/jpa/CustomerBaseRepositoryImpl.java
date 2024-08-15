package com.demo.core.config.jpa;

import com.demo.core.exception.ErrorCode;
import com.demo.core.exception.GlobalException;
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
    public <R> List<R> customQuery(CustomQuery customQuery, Class<R> clazz) {
        if (customQuery == null || customQuery.getSql() == null)
            throw new GlobalException(ErrorCode.CODE_ERROR_PARAMS_NOT_FOUND);
        Query query = entityManager.createNativeQuery(customQuery.getSql(), clazz);
        if (customQuery.getParams() != null && !customQuery.getParams().isEmpty()) {
            for (int i = 0, l = customQuery.getParams().size(); i < l; i++)
                query.setParameter(i + 1, customQuery.getParams().get(i));

        }
        return query.getResultList();
    }

    @Override
    public List<T> customQuery(CustomQuery query) {
        return this.customQuery(query, this.clazz);
    }

    @Override
    public List<T> customQuery(CustomCriteriaQuery criteriaQuery) {
        return this.customQuery(criteriaQuery, this.clazz);
    }

    @Override
    public <R> List<R> customQuery(CustomCriteriaQuery criteriaQuery, Class<R> clazz) {
        if (criteriaQuery == null || criteriaQuery.getQuerySql() == null)
            throw new GlobalException(ErrorCode.CODE_ERROR_PARAMS_NOT_FOUND);
        if (criteriaQuery.getParams() != null && !criteriaQuery.getParams().isEmpty()) {
            StringBuilder sqlStr = new StringBuilder(criteriaQuery.getQuerySql());
            boolean where = false;
            for (QueryCriteria criteria : criteriaQuery.getParams()) {
                if (criteria.getParameter() != null) {
                    if (!where) {
                        sqlStr.append(" where ");
                        where = true;
                    } else {
                        sqlStr.append(" and ");
                    }
                    sqlStr.append(criteria.whereSql());
                }
            }
            Query query = entityManager.createNativeQuery(sqlStr.toString(), clazz);
            if (criteriaQuery.getParams() != null && !criteriaQuery.getParams().isEmpty()) {
                int index = 1;
                for (QueryCriteria criteria : criteriaQuery.getParams()) {
                    index = criteria.setQueryParameter(query,index);
                }
            }
            return query.getResultList();
        }
        return entityManager.createNativeQuery(criteriaQuery.getQuerySql(), clazz).getResultList();

    }

    @Override
    public Page<T> customQuery(CustomCriteriaQuery query, Pageable pageable) {
        return this.customQuery(query, this.clazz, pageable);
    }


    @Override
    public Page<T> customQuery(CustomQuery query, Pageable pageable) {
        return this.customQuery(query, this.clazz, pageable);
    }

    @Override
    public <R> Page<R> customQuery(CustomQuery customQuery, Class<R> clazz, Pageable pageable) {
        if (customQuery == null || customQuery.getSql() == null)
            throw new GlobalException(ErrorCode.CODE_ERROR_PARAMS_NOT_FOUND);
        String querySql = customQuery.getSql();
        String countSql = "SELECT COUNT(0) " + querySql.toString().substring(querySql.toString().toLowerCase().indexOf("from"));
        querySql += getSortStr(pageable);
        Query countQuery = entityManager.createNativeQuery(countSql);
        Query listQuery = entityManager.createNativeQuery(querySql.toString(), clazz);
        if (customQuery.getParams() != null && !customQuery.getParams().isEmpty()) {
            for (int i = 0, l = customQuery.getParams().size(); i < l; i++) {
                countQuery.setParameter(i + 1, customQuery.getParams().get(i));
                listQuery.setParameter(i + 1, customQuery.getParams().get(i));
            }
        }
        listQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        listQuery.setMaxResults(pageable.getPageSize());
        return new PageImpl<>(listQuery.getResultList(), pageable, (long) countQuery.getSingleResult());
    }


    @Override
    public <R> Page<R> customQuery(CustomCriteriaQuery criteriaQuery, Class<R> clazz, Pageable pageable) {
        if (criteriaQuery == null || criteriaQuery.getQuerySql() == null || criteriaQuery.getCountSql() == null)
            throw new GlobalException(ErrorCode.CODE_ERROR_PARAMS_NOT_FOUND);
        String querySql = criteriaQuery.getQuerySql();
        String countSql = criteriaQuery.getCountSql();
        if (criteriaQuery.getParams() != null && !criteriaQuery.getParams().isEmpty()) {
            StringBuilder querySqlStr = new StringBuilder(querySql);
            StringBuilder countSqlStr = new StringBuilder(countSql);
            boolean where = false;
            for (QueryCriteria criteria : criteriaQuery.getParams()) {
                if (criteria.getParameter() != null) {
                    if (!where) {
                        querySqlStr.append(" where ");
                        countSqlStr.append(" where ");
                        where = true;
                    } else {
                        querySqlStr.append(" and ");
                        countSqlStr.append(" and ");
                    }
                    querySqlStr.append(criteria.whereSql());
                    countSqlStr.append(criteria.whereSql());
                }
            }
            querySql = querySqlStr.toString();
            countSql = countSqlStr.toString();
        }
        if (criteriaQuery.getGroupBy() != null) {
            querySql += criteriaQuery.getGroupBy();
        }
        querySql += getSortStr(pageable);
        Query countQuery = entityManager.createNativeQuery(countSql);
        Query listQuery = entityManager.createNativeQuery(querySql.toString(), clazz);
        if (criteriaQuery.getParams() != null && !criteriaQuery.getParams().isEmpty()) {
            int index = 1;
            for (QueryCriteria criteria : criteriaQuery.getParams()) {
                int lineIndex = criteria.setQueryParameter(listQuery,index);
                criteria.setQueryParameter(countQuery,index);
                index = lineIndex;
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
