package com.demo.core.config.jpa;

import lombok.Getter;

import java.util.List;

/**
 * 自定义查询参数
 */
@Getter
public class CustomQuery {
    private final String sql;

    private List<Object> params;

    public CustomQuery(String sql) {
        this.sql = sql;
    }

    public CustomQuery(String sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
    }
}
