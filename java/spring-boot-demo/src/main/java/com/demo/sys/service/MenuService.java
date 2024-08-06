package com.demo.sys.service;

import com.demo.core.service.impl.DefaultCURDService;
import com.demo.sys.datasource.entity.SysMenu;
import com.demo.sys.datasource.entity.SysUser;
import org.springframework.stereotype.Service;

@Service("menuService")
public class MenuService extends DefaultCURDService<SysMenu> {

}
