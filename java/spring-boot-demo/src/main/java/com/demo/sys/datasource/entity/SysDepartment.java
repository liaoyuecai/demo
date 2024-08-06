package com.demo.sys.datasource.entity;


import com.demo.core.entity.TableBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "sys_department")
public class SysDepartment extends TableBaseEntity {
    private String departmentName;
    private Integer departmentType;
    private String description;
    private Integer parentId;
}