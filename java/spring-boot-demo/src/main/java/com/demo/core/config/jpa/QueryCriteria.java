package com.demo.core.config.jpa;

import lombok.Getter;

import java.util.List;

/**
 * 查询条件，用于自定义查询
 * 扩展Example，解决Example不能使用大于小于的问题
 */
@Getter
public class QueryCriteria {
    /**
     * 查询字段
     */
    private String columnName;

    private Expression expression;

    public QueryCriteria(String columnName, Expression expression, Object parameter) {
        this.columnName = columnName;
        this.expression = expression;
        this.parameter = parameter;
    }

    /**
     * 条件表达式
     */
    public enum Expression {
        //=
        EQUALS,
        // 为空
        IS_NULL,
        // 不为空
        IS_NOT_NULL,
        // 不等于
        NOT_EQUALS,
        // like(%?)
        START_WITH,
        // like(?%)
        END_WITH,
        // like(%?%)
        CONTAINS,
        // >
        GREATER_THAN,
        // >=
        GREATER_THAN_EQUALS,
        // <
        LESS_THAN,
        // <=
        LESS_THAN_EQUALS,
        IN,
        AND,OR;
    }

    /**
     * 条件匹配值
     */
    private Object parameter;

    /**
     * 生成sql查询语句
     *
     * @return
     */
    public String whereSql() {
        StringBuilder sqlStr = new StringBuilder(this.columnName);
        switch (this.expression) {
            case EQUALS -> sqlStr.append(" = ? ");
            case IS_NULL -> sqlStr.append(" is null ");
            case IS_NOT_NULL -> sqlStr.append(" is not null ");
            case NOT_EQUALS -> sqlStr.append(" != ? ");
            case START_WITH -> {
                sqlStr.append(" like ? ");
                this.parameter = this.getParameter() == null ? null : "%" + this.getParameter();
            }
            case END_WITH -> {
                sqlStr.append(" like ? ");
                this.parameter = this.getParameter() == null ? null : this.getParameter() + "%";
            }
            case CONTAINS -> {
                sqlStr.append(" like ? ");
                this.parameter = this.getParameter() == null ? null : "%" + this.getParameter() + "%";
            }
            case GREATER_THAN -> sqlStr.append(" > ? ");
            case GREATER_THAN_EQUALS -> sqlStr.append(" >= ? ");
            case LESS_THAN -> sqlStr.append(" < ? ");
            case LESS_THAN_EQUALS -> sqlStr.append(" <= ? ");
            case IN -> sqlStr.append(" in (?) ");
            case OR ->{
                List<QueryCriteria> params = (List<QueryCriteria>)this.parameter;
                sqlStr.append(" ( ");
                if (params!=null && params.size()>1){
                    boolean flag = false;
                    for(QueryCriteria criteria:params){
                        sqlStr.append(criteria.whereSql());
                        if (!flag){
                            sqlStr.append(" or ");
                            flag = true;
                        }
                    }
                }
                sqlStr.append(" ) ");
            }
            case AND ->{
                List<QueryCriteria> params = (List<QueryCriteria>)this.parameter;
                sqlStr.append(" ( ");
                if (params!=null && params.size()>1){
                    boolean flag = false;
                    for(QueryCriteria criteria:params){
                        sqlStr.append(criteria.whereSql());
                        if (!flag){
                            sqlStr.append(" and ");
                            flag = true;
                        }
                    }
                }
                sqlStr.append(" ) ");
            }
        }
        return sqlStr.toString();
    }

}
