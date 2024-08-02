package com.demo.core.config.jpa;

import com.demo.core.service.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 自定义的JPA Repository。准备了一些可能常用的函数
 *
 * @param <T>
 */
@NoRepositoryBean
@Transactional
public interface CustomerBaseRepository<T> extends JpaRepository<T, Integer>, JpaSpecificationExecutor<T> {


    /**
     * 根据id修改删除标记。此函数只针对有删除标志字段的表。这里的删除标志默认为deleted=1
     *
     * @param ids
     * @return
     */
    int deleteUpdateByIds(@Param("ids") Collection<Integer> ids);

    /**
     * 直接输入原生sql查询
     *
     * @param sql    查询语句
     * @param clazz  返回实体类型
     * @param params 查询参数列表
     * @param <R>
     * @return
     */
    <R> List<R> customQuery(String sql, Class<R> clazz, List<Object> params);

    /**
     * 直接输入原生sql查询(复杂条件)
     * 返回类型与entity一致
     *
     * @param sql
     * @param criteria
     * @return
     */
    List<T> customQueryCriteria(String sql, List<QueryCriteria> criteria);

    /**
     * 直接输入原生sql查询(复杂条件)
     * 返回类型与entity可以不一致
     *
     * @param sql
     * @param clazz
     * @param criteria
     * @return
     */
    <R> List<R> customQueryCriteria(String sql, Class<R> clazz, List<QueryCriteria> criteria);

    /**
     * 直接输入原生sql查询(复杂条件)
     * 分页
     * 返回类型与entity一致
     *
     * @param sql
     * @param criteria
     * @return
     */
    Page<T> customQueryCriteriaPage(String sql, List<QueryCriteria> criteria, Pageable pageable);

    /**
     * 直接输入原生sql查询(复杂条件)
     * 分页
     * 返回类型与entity可以不一致
     *
     * @param sql
     * @param clazz
     * @param criteria
     * @return
     */
    <R> Page<R> customQueryCriteriaPage(String sql, Class<R> clazz, List<QueryCriteria> criteria, Pageable pageable);


    /**
     * 查询未被软删除数据。一般用于有软删除的数据管理页面
     *
     * @return
     */
    List<T> findNotDeleted();

    /**
     * 查询未被软删除并且状态为真的数据。一般用于非管理的展示数据
     *
     * @return
     */
    List<T> findNotDeletedAndStatus();


    void deleteByIds(Collection<Integer> ids);
}
