package com.demo.core.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注查询数据时需要数据填充
 * 一般填充status，deleted,默认是deleted为0，不查询已经软删除的数据
 * 需要请求的类的data继承TableBaseEntity
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestSelect {
    /**
     * 需要填充的参数位于所有参数中的索引，默认0，即第一个
     *
     * @return
     */
    int index() default 0;

    /**
     * -1时不限制
     *
     * @return
     */
    int status() default -1;

}
