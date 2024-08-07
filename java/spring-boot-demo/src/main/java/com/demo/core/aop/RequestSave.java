package com.demo.core.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注保存数据时需要数据填充
 * 一般填充创建时间、创建人id、修改时间、修改人id等
 * 需要请求的类的data继承TableBaseEntity
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestSave {
    /**
     * 需要填充的参数位于所有参数中的索引，默认0，即第一个
     * @return
     */
    int index() default 0;
}
