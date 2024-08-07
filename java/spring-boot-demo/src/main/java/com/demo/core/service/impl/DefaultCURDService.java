package com.demo.core.service.impl;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.core.dto.PageList;
import com.demo.core.dto.PageListRequest;
import com.demo.core.exception.ErrorCode;
import com.demo.core.exception.GlobalException;
import com.demo.core.service.CURDService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

/**
 * 默认实现CURDService
 * 使用时须继承
 * 适用与有其他逻辑需要处理的时候
 */
public abstract class DefaultCURDService<T> implements CURDService<T> {

    protected CustomerBaseRepository<T> repository;

    public DefaultCURDService(CustomerBaseRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    public T insert(T entity) {
        return repository.save(entity);
    }

    @Override
    public T update(T entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<Integer> ids) {
        repository.deleteByIds(ids);
    }

    @Override
    public void deleteUpdate(Integer id) {
        repository.deleteUpdateByIds(List.of(id));
    }

    @Override
    public void deleteUpdate(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) throw new GlobalException(ErrorCode.PARAMS_ERROR_REQUEST_DATA_NOT_FOUND);
        repository.deleteUpdateByIds(ids);
    }

    @Override
    public List<T> findList(Example<T> example) {
        return repository.findAll(example);
    }

    @Override
    public Page<T> findPage(Example<T> example, Pageable pageable) {
        return repository.findAll(example, pageable);
    }

    @Override
    public PageList<T> findPage(PageListRequest<T> pageListRequest) {
        if (pageListRequest.toExample() != null)
            return pageListRequest.toPageList(repository.findAll(pageListRequest.toExample(), pageListRequest.toPageable()));
        else
            return pageListRequest.toPageList(repository.findAll(pageListRequest.toPageable()));
    }

    @Override
    public PageList<T> findPageCriteria(PageListRequest pageListRequest) {
        return pageListRequest.toPageList(repository.customQueryCriteriaPage(pageListRequest.toQuerySql(), pageListRequest.toCriteria(), pageListRequest.toPageable()));
    }
}
