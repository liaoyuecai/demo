package com.demo.core.service;

import com.demo.core.dto.PageListRequest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

/**
 * 用于不太复杂的数据库增删改查接口
 * 如果有CURD操作可以继承此类
 */
public interface CURDService<T> {
    T insert(T entity);

    T update(T entity);

    void delete(Integer id);

    void deleteAll(Collection<Integer> ids);

    void deleteUpdate(Integer id);

    void deleteUpdate(Collection<Integer> ids);

    List<T> findList(Example<T> example);

    Page<T> findPage(Example<T> example, Pageable pageable);

    Page<T> findPageExample(PageListRequest<T> pageListRequest);

    Page<T> findPageCriteria(PageListRequest pageListRequest);
}
