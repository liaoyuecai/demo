package com.demo.sys.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "sys_user_department")
public class SysUserDepartment {
    /**
     * id
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    protected Integer id;
    private Integer userId;
    private Integer departmentId;
}