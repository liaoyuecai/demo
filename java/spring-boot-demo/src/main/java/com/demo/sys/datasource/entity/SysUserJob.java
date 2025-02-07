package com.demo.sys.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "sys_user_job")
public class SysUserJob {
    /**
     * id
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    protected Integer id;
    private Integer userId;
    private Integer jobId;

    public SysUserJob() {
    }

    public SysUserJob(Integer userId, Integer jobId) {
        this.userId = userId;
        this.jobId = jobId;
    }
}