package com.demo.core.service;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.core.dto.PageList;
import com.demo.core.dto.PageListRequest;
import com.demo.core.exception.ErrorCode;
import com.demo.core.exception.GlobalException;
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
public abstract class CURDService<T, R extends CustomerBaseRepository<T>> {

    protected R repository;

    public CURDService(R repository) {
        this.repository = repository;
    }

    public T save(T entity) {
        return repository.save(entity);
    }


    public void delete(Integer id) {
        repository.deleteById(id);
    }


    public void deleteAll(Collection<Integer> ids) {
        repository.deleteByIds(ids);
    }


    public void deleteUpdate(Integer id) {
        repository.deleteUpdateByIds(List.of(id));
    }


    public void deleteUpdate(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) throw new GlobalException(ErrorCode.PARAMS_ERROR_REQUEST_DATA_NOT_FOUND);
        repository.deleteUpdateByIds(ids);
    }


    public List<T> findList(Example<T> example) {
        return repository.findAll(example);
    }



    public List<T> findNotDeletedAndStatus() {
        return repository.findNotDeletedAndStatus();
    }

    public List<T> findNotDeleted() {
        return repository.findNotDeleted();
    }


    public Page<T> findPage(Example<T> example, Pageable pageable) {
        return repository.findAll(example, pageable);
    }


    public PageList<T> findPage(PageListRequest<T> request) {
        if (request.toExample() != null)
            return request.toPageList(repository.findAll(request.toExample(), request.toPageable()));
        else
            return request.toPageList(repository.findAll(request.toPageable()));
    }

    /**
     * 自定义查询
     * 参数来自PageListRequest.getCustomQuery
     *
     * @param request
     * @return
     */
    public PageList<T> findPageCustom(PageListRequest<T> request) {
        return request.toPageList(repository.customQuery(request.getCustomQuery(), request.toPageable()));
    }

    /**
     * 自定义查询
     * 参数来自PageListRequest.getCustomQuery
     *
     * @param request
     * @param clazz
     * @param <K>
     * @return
     */
    public <K> PageList<K> findPageCustom(PageListRequest<T> request, Class<K> clazz) {
        Page<K> page = repository.customQuery(request.getCustomQuery(), clazz, request.toPageable());
        PageList<K> pageList = new PageList<>(request);
        pageList.setTotal(page.getTotalElements());
        pageList.setList(page.getContent());
        return pageList;
    }

    /**
     * 自定义查询
     * 参数来自PageListRequest.getCustomCriteriaQuery
     *
     * @param request
     * @return
     */
    public PageList<T> findPageCustomCriteria(PageListRequest<T> request) {
        return request.toPageList(repository.customQuery(request.getCustomCriteriaQuery(), request.toPageable()));
    }


    /**
     * 自定义查询
     * 参数来自PageListRequest.getCustomCriteriaQuery
     *
     * @param request
     * @param clazz
     * @param <K>
     * @return
     */
    public <K> PageList<K> findPageCustomCriteria(PageListRequest<T> request, Class<K> clazz) {
        Page<K> page = repository.customQuery(request.getCustomCriteriaQuery(), clazz, request.toPageable());
        PageList<K> pageList = new PageList<>(request);
        pageList.setTotal(page.getTotalElements());
        pageList.setList(page.getContent());
        return pageList;
    }

}
