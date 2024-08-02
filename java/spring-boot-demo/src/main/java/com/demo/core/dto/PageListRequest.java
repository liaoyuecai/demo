package com.demo.core.dto;


import com.demo.core.exception.ErrorCode;
import com.demo.core.exception.GlobalException;
import com.demo.core.utils.StringUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 有页码的数据返回实体请求
 * <p>
 * 返回一般为PageList
 * 单个表的有复杂查询的直接继承此类，覆盖toExample函数
 * Example解决不了的查询，重写toCriteria，使用Criteria时，data不必与查询类型一直，使用Example时需要一致
 *
 * @param <T>
 */
@Getter
@Setter
public class PageListRequest<T> extends ApiHttpRequest<T> {
    private Integer pageSize;
    private Integer current;
    private List<PageOrder> orders;


    public Pageable toPageable() {

        if (orders != null && !orders.isEmpty())
            return PageRequest.of(this.getCurrent() - 1, this.getPageSize(),
                    Sort.by(orders.stream().map(i -> i.convert()).toList()));
        return PageRequest.of(this.getCurrent() - 1, this.getPageSize());
    }

    public Example toExample() {
        return Example.of(this.data);
    }

    public PageList<T> toPageList(Page<T> page) {
        PageList<T> pageList = new PageList<>(this);
        pageList.setList(page.toList());
        pageList.setTotal(page.getTotalElements());
        return pageList;
    }

    public PageList<T> toPageList() {
        PageList<T> pageList = new PageList<>(this);
        return pageList;
    }


    public List<T> toCriteria() {
        return List.of();
    }

    /**
     * 自定义查询时生成sql，复杂sql须重写
     *
     * @return
     */
    public String toQuerySql() {
        Type type = this.getClass().getGenericSuperclass();
        if (!(type instanceof ParameterizedType))
            throw new GlobalException(ErrorCode.CODE_ERROR, "调用父类toQuerySql函数时必须继承并指定泛型");
        Class<?> clazz = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
        if (!clazz.isAnnotationPresent(Entity.class))
            throw new GlobalException(ErrorCode.CODE_ERROR, clazz.getName() + "has not annotation @Entity");
        StringBuilder sql = new StringBuilder("SELECT ");
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0, l = fields.length; i < l; i++) {
            Field field = fields[i];
            sql.append(field.isAnnotationPresent(Column.class) ?
                    field.getAnnotation(Column.class).name() :
                    StringUtils.toUnderscoreCase(field.getName()));
            if (i != l - 1)
                sql.append(",");
        }
        sql.append(" FROM ").append(clazz.isAnnotationPresent(Table.class) ? clazz.getAnnotation(Table.class).name() :
                StringUtils.toUnderscoreCase(clazz.getSimpleName()));
        return sql.toString();
    }


}
