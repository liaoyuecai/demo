package com.demo.sys.service;

import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.DeleteRequest;
import com.demo.core.exception.ErrorCode;
import com.demo.core.exception.GlobalException;
import com.demo.core.service.CURDService;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dao.SysMenuRepository;
import com.demo.sys.datasource.dao.SysRoleMenuRepository;
import com.demo.sys.datasource.dao.SysRoleRepository;
import com.demo.sys.datasource.dto.RoleBindMenu;
import com.demo.sys.datasource.entity.SysRole;
import com.demo.sys.datasource.entity.SysRoleMenu;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("roleService")
@Transactional
public class RoleService extends CURDService<SysRole, SysRoleRepository> {

    @Resource
    SysRoleMenuRepository roleMenuRepository;
    @Resource
    SysMenuRepository menuRepository;


    public RoleService(@Autowired SysRoleRepository repository) {
        super(repository);
    }

    @Override
    public SysRole save(ApiHttpRequest<SysRole> request) {
        if (request.getData().getRoleType() == 0) {
            throw new GlobalException(ErrorCode.ACCESS_DATA_UPDATE_ERROR);
        }
        return super.save(request);
    }


    public void bindMenu(ApiHttpRequest<RoleBindMenu> request) {
        if (!request.getUser().isRoot())
            checkCreateId(request.getData().getRoleId(), request.getUser().getId());
        roleMenuRepository.deleteByRoleId(request.getData().getRoleId());
        roleMenuRepository.saveAll(request.getData().getMenuIds().stream()
                .map(i -> new SysRoleMenu(request.getData().getRoleId(), i)).toList());
    }

    public List<Integer> findBindMenusId(Integer roleId) {
        return menuRepository.findIdByRoleId(roleId);
    }
}
