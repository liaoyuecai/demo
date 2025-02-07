package com.demo.sys.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "sys_role_permission")
public class SysRolePermission {
    /**
     * id
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    protected Integer id;
    private Integer roleId;
    private Integer permissionId;
}