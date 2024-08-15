package com.demo.core.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求数据设置
 * 用于设置CURD请求中的创建人，创建时间，修改人，修改时间等
 * 可设置查询请求时查询数据的deleted与status属性
 * 可设置更新或者删除数据时要求创建人与当前用户一直
 * 可设置查询时只查询当前用户创建数据
 * 需要请求的类的data继承TableBaseEntity
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBaseEntitySet {
    /**
     * CURD函数类型
     * @return
     */
    RequestSetType type() default RequestSetType.SAVE;

    /**
     * 需要填充的参数位于所有参数中的索引，默认0，即第一个
     *
     * @return
     */
    int index() default 0;

    /**
     * 是否校验对数据进行操作的为数据创建人
     * 是否设置只查询当前用户创建的数据
     * 是否设置只能删除当前用户创建的数据
     *
     * @return
     */
    boolean checkCreateBy() default false;

    /**
     * 查询数据的status值，-1时不限制
     * deleted为0，不查询已经软删除的数据
     *
     * @return
     */
    int status() default -1;
}
