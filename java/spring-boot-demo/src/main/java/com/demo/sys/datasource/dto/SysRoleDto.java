package com.demo.sys.datasource.dto;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


@Data
@Entity
public class SysRoleDto {

    @Id
    private Integer id;
    private String roleName;
    private String description;
    private Integer status;
    private String roleKey;
    private String menus;

}
