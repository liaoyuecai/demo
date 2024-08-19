package com.demo.sys.datasource.dto;

import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.entity.SysMenu;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CurrentUser {
    private String name;
    private String avatar;
    private String email;
    private String phone;
    private boolean root;
    private List<SysMenu> menuList;

    public CurrentUser(AuthUserCache cache) {
        this.name = cache.getUsername();
        this.avatar = cache.getAvatar();
        this.email = cache.getEmail();
        this.phone = cache.getPhone();
        this.root = cache.isRoot();
        this.menuList = new ArrayList<>();
        if (cache.getMenuList() != null)
            this.menuList = cache.getMenuList().stream()
                    .map(i -> {
                        SysMenu menu = new SysMenu();
                        menu.setMenuName(i.getMenuName());
                        menu.setMenuPath(i.getMenuPath());
                        menu.setMenuIcon(i.getMenuIcon());
                        menu.setId(i.getId());
                        menu.setParentId(i.getParentId());
                        return menu;
                    })
                    .toList();
    }
}
