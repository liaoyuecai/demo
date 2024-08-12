package com.demo.core.config.jpa;

import lombok.Getter;

import java.util.List;

/**
 * 自定义查询参数
 */
@Getter
public class CustomCriteriaQuery {
    private final String querySql;
    private String countSql;

    private List<QueryCriteria> params;

    public CustomCriteriaQuery(String querySql) {
        this.querySql = querySql;
    }

    public CustomCriteriaQuery(String querySql, List<QueryCriteria> params) {
        this.querySql = querySql;
        this.params = params;
    }

    public CustomCriteriaQuery(String querySql, String countSql) {
        this.querySql = querySql;
        this.countSql = countSql;
    }

    public CustomCriteriaQuery(String querySql, String countSql, List<QueryCriteria> params) {
        this.querySql = querySql;
        this.countSql = countSql;
        this.params = params;
    }
}
