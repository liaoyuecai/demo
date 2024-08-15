package com.demo.core.service;

import com.demo.core.aop.RequestBaseEntitySet;
import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.DeleteRequest;
import com.demo.core.dto.PageList;
import com.demo.core.dto.PageListRequest;
import com.demo.core.entity.TableBaseEntity;
import com.demo.core.exception.ErrorCode;
import com.demo.core.exception.GlobalException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

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

    public T save(ApiHttpRequest<T> request) {
        if (!request.getUser().isRoot()) {
            T entity = request.getData();
            RequestBaseEntitySet entitySet = request.getEntitySet();
            if (entity instanceof TableBaseEntity &&
                    ((TableBaseEntity) entity).getId() != null &&
                    entitySet != null && entitySet.checkCreateBy()) {
                TableBaseEntity baseEntity = (TableBaseEntity) entity;
                Optional<T> optional = repository.findById(baseEntity.getId());
                if (optional.isPresent() &&
                        !((TableBaseEntity) optional.get()).getCreateBy().equals(
                                request.getUser().getId())) {
                    throw new GlobalException(ErrorCode.ACCESS_DATA_UPDATE_ERROR);
                }
            }
        }
        return repository.save(request.getData());
    }


    public void delete(ApiHttpRequest<Integer> request) {
        if (!request.getUser().isRoot())
            checkDelete(request);
        repository.deleteById(request.getData());
    }


    public void deleteAll(DeleteRequest request) {
        if (!request.getUser().isRoot())
            checkDeleteAll(request);
        repository.deleteByIds(request.getData());
    }


    public void deleteUpdate(ApiHttpRequest<Integer> request) {
        if (!request.getUser().isRoot())
            checkDelete(request);
        repository.deleteUpdateByIds(List.of(request.getData()));
    }


    public void deleteUpdate(DeleteRequest request) {
        List<Integer> ids = request.getData();
        if (ids == null || ids.isEmpty()) throw new GlobalException(ErrorCode.PARAMS_ERROR_REQUEST_DATA_NOT_FOUND);
        if (!request.getUser().isRoot())
            checkDeleteAll(request);
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
    public <K, R> PageList<K> findPageCustomCriteria(PageListRequest<R> request, Class<K> clazz) {
        Page<K> page = repository.customQuery(request.getCustomCriteriaQuery(), clazz, request.toPageable());
        PageList<K> pageList = new PageList<>(request);
        pageList.setTotal(page.getTotalElements());
        pageList.setList(page.getContent());
        return pageList;
    }

    void checkDelete(ApiHttpRequest<Integer> request) {
        RequestBaseEntitySet entitySet = request.getEntitySet();
        if (entitySet != null && entitySet.checkCreateBy()) {
            checkCreateId(request.getData(), request.getUser().getId());
        }
    }

    void checkDeleteAll(DeleteRequest request) {
        RequestBaseEntitySet entitySet = request.getEntitySet();
        if (entitySet != null && entitySet.checkCreateBy()) {
            checkCreateId(request.getData(), request.getUser().getId());
        }
    }

    protected void checkCreateId(Integer id, Integer userId) {
        Optional<T> optional = repository.findById(id);
        if (optional.isPresent() && optional.get() instanceof TableBaseEntity) {
            TableBaseEntity entity = (TableBaseEntity) optional.get();
            if (!entity.getCreateBy().equals(userId))
                throw new GlobalException(ErrorCode.ACCESS_DATA_UPDATE_ERROR);
        }
    }

    protected void checkCreateId(List<Integer> ids, Integer userId) {
        List<T> list = repository.findAllById(ids);
        for (T t : list) {
            if (t instanceof TableBaseEntity) {
                TableBaseEntity entity = (TableBaseEntity) t;
                if (!entity.getCreateBy().equals(userId))
                    throw new GlobalException(ErrorCode.ACCESS_DATA_UPDATE_ERROR);
            }
        }
    }
}
