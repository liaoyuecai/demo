package com.demo.sys.service;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.core.service.impl.DefaultCURDService;
import com.demo.sys.datasource.dao.SysMenuRepository;
import com.demo.sys.datasource.entity.SysMenu;
import com.demo.sys.datasource.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("menuService")
public class MenuService extends DefaultCURDService<SysMenu> {

    public MenuService(@Autowired SysMenuRepository repository) {
        super(repository);
    }

    @Override
    public void deleteUpdate(Integer id) {
        //todo 后期要验证角色关联
        super.deleteUpdate(id);
    }
}
