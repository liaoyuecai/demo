package com.demo.core.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 集成实体表一些通用属性
 */
@Getter
@Setter
@MappedSuperclass
public class TableBaseEntity {
    /**
     * id
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    protected Integer id;
    /**
     * 状态 1是0否
     */
    protected Integer status;
    /**
     * 创建时间
     */
    protected LocalDateTime createTime;
    /**
     * 创建人
     */
    protected Integer createBy;
    /**
     * 修改时间
     */
    protected LocalDateTime updateTime;
    /**
     * 修改人
     */
    protected Integer updateBy;
    /**
     * 删除标识 1删除 0正常
     */
    protected Integer deleted;
}
