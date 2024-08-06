package com.demo.sys.datasource.entity;

import com.demo.core.entity.TableBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "sys_job")
public class SysJob extends TableBaseEntity {

    private String jobName;
    private String description;
    private Integer deptId;

}