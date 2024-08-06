package com.demo.sys.datasource.dto;

import com.demo.sys.datasource.entity.SysMenu;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 前端的菜单目录结构
 */
@Getter
public class WebMenu {
    private String key;
    private Integer parentId;
    private String name;
    private String icon;
    private String path;
    private List<WebMenu> children;

    public WebMenu(SysMenu menu) {
        this.key = menu.getMenuName();
        this.parentId = menu.getParentId();
        this.name = menu.getMenuName();
        this.path = menu.getMenuPath();
        this.icon = menu.getMenuIcon();
        this.children = new ArrayList<>();
    }
}
