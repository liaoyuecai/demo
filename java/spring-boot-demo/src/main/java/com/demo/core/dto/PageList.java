package com.demo.core.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 有页码的数据返回实体
 *
 * @param <T>
 */
@Getter
@Setter
public class PageList<T> {
    private Integer pageSize;
    private Integer current;
    private Long total = 0l;
    private List<T> list;


    public PageList(PageListRequest request) {
        this.current = request.getCurrent();
        this.pageSize = request.getPageSize();
    }

    public PageList(PageListRequest request, Page page) {
        this.current = request.getCurrent();
        this.pageSize = request.getPageSize();
        this.total = page.getTotalElements();
    }

}
