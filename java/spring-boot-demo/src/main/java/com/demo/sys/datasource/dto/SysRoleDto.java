package com.demo.sys.datasource.dto;


import com.demo.sys.datasource.entity.SysRole;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;


@Data
public class SysRoleDto {
    @Id
    private Integer id;
    private String roleName;
    private String description;
    private Integer status;
    private Integer roleType;
    private String roleKey;
    private Collection<Integer> menuIds;

    public SysRoleDto() {
    }

    public SysRoleDto(SysRole role) {
        this.id = role.getId();
        this.roleName = role.getRoleName();
        this.description = role.getDescription();
        this.status = role.getStatus();
        this.roleType = role.getRoleType();
        this.roleKey = role.getRoleKey();
        this.menuIds = new ArrayList<>();
    }

    public void addMenuIds(int id) {
        this.menuIds.add(id);
    }
}
