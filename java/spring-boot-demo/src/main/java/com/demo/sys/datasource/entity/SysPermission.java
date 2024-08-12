package com.demo.sys.datasource.entity;

import com.demo.core.entity.TableBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "sys_permission")
public class SysPermission extends TableBaseEntity {

    private String permissionName;
    private String permissionUrl;
    private String description;
    private Integer permissionType;

}