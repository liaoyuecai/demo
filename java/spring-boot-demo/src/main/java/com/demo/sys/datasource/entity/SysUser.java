package com.demo.sys.datasource.entity;

import com.demo.core.entity.TableBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "sys_user")
public class SysUser extends TableBaseEntity {

    private String realName;
    private String userAvatar;
    private String username;
    private String password;
    private String email;
    private String phone;

}