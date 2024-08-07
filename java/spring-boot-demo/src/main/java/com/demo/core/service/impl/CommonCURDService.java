package com.demo.core.service.impl;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.core.dto.PageList;
import com.demo.core.dto.PageListRequest;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 更懒一点的写法
 * 适合就只有curd的用法
 * 直接在controller调用
 */
@Service
public class CommonCURDService {

    @Resource
    private ApplicationContext context;

    public <T, R extends CustomerBaseRepository<T>> T insert(T entity, Class<R> repositoryClass) {
        return context.getBean(repositoryClass).save(entity);
    }


    public <T, R extends CustomerBaseRepository<T>> T update(T entity, Class<R> repositoryClass) {
        return context.getBean(repositoryClass).save(entity);
    }


    public <T, R extends CustomerBaseRepository<T>> void delete(Integer id, Class<R> repositoryClass) {
        context.getBean(repositoryClass).deleteById(id);
    }


    public <T, R extends CustomerBaseRepository<T>> void deleteUpdate(Integer id, Class<R> repositoryClass) {
        context.getBean(repositoryClass).deleteUpdateByIds(List.of(id));
    }


    public <T, R extends CustomerBaseRepository<T>> List<T> findList(Example<T> example, Class<R> repositoryClass) {
        return context.getBean(repositoryClass).findAll(example);
    }


    public <T, R extends CustomerBaseRepository<T>> Page<T> findPage(Example<T> example, Pageable pageable, Class<R> repositoryClass) {
        return context.getBean(repositoryClass).findAll(example, pageable);
    }

    public <T, R extends CustomerBaseRepository<T>> PageList<T> findPage(PageListRequest<T> pageListRequest, Class<R> repositoryClass) {
        if (pageListRequest.toExample() != null)
            return pageListRequest.toPageList(context.getBean(repositoryClass).findAll(pageListRequest.toExample(), pageListRequest.toPageable()));
        else
            return pageListRequest.toPageList(context.getBean(repositoryClass).findAll(pageListRequest.toPageable()));
    }


    public <R extends CustomerBaseRepository> PageList findPageCriteria(PageListRequest pageListRequest, Class<R> repositoryClass) {
        return pageListRequest.toPageList(context.getBean(repositoryClass).customQueryCriteriaPage(pageListRequest.toQuerySql(), pageListRequest.toCriteria(), pageListRequest.toPageable()));
    }


    public <T, R extends CustomerBaseRepository<T>> void deleteUpdate(Collection ids, Class<R> repositoryClass) {
        context.getBean(repositoryClass).deleteUpdateByIds(ids);
    }


    public <T, R extends CustomerBaseRepository<T>> void deleteAll(Collection ids, Class<R> repositoryClass) {
        context.getBean(repositoryClass).deleteByIds(ids);
    }
}
