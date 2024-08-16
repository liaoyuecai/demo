package com.demo.sys.datasource.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
@Entity
@Getter
@Setter
public class SysUserDto {
    @Id
    private Integer id;
    private String realName;
    private String username;
    private String phone;
    private Integer status;
    private String jobs;
    private String roles;
}
