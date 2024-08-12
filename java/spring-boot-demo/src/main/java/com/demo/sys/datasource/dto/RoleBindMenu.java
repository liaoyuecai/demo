package com.demo.sys.datasource.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 角色绑定菜单
 */
@Getter
@Setter
public class RoleBindMenu {
    private Integer roleId;
    private List<Integer> menuIds;
}
