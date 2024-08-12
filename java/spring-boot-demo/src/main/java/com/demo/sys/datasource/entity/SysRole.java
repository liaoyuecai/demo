package com.demo.sys.datasource.entity;

import com.demo.core.entity.TableBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "sys_role")
public class SysRole extends TableBaseEntity {

    private String roleName;
    private String roleKey;
    private String description;


}