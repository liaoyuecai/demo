package com.demo.sys.service;

import com.demo.core.authentication.UserDatasourceService;
import com.demo.core.exception.ErrorCode;
import com.demo.core.exception.GlobalException;
import com.demo.core.utils.StringUtils;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dao.SysMenuRepository;
import com.demo.sys.datasource.dao.SysRoleRepository;
import com.demo.sys.datasource.dao.SysUserRepository;
import com.demo.sys.datasource.dto.ResetRootPassword;
import com.demo.sys.datasource.entity.SysRole;
import com.demo.sys.datasource.entity.SysUser;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthUserService implements UserDatasourceService {

    @Resource
    SysUserRepository userRepository;
    @Resource
    SysRoleRepository roleRepository;
    @Resource
    SysMenuRepository menuRepository;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Value("${user.root.username:'root'}")
    private String rootAccount;
    @Value("${user.root.resetCode}")
    private String rootResetCode;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userRepository.findByUsernameAndStatusAndDeleted(username, 1, 0);
        if (user == null) return null;
        AuthUserCache userCache = new AuthUserCache(user);
        if (rootAccount.equals(user.getUsername())) userCache.setRoot(true);
        return userCache;
    }

    @Override
    public void loadUserAuthority(UserDetails details) {
        AuthUserCache userCache = (AuthUserCache) details;
        if (userCache.isRoot()) {
            userCache.setRoleList(roleRepository.findNotDeletedAndStatus());
            userCache.setMenuList(menuRepository.findByRoot());
        } else {
            userCache.setRoleList(roleRepository.findRolesByUserId(userCache.getId()));
            if (userCache.getRoleList() != null && !userCache.getRoleList().isEmpty())
                userCache.setMenuList(menuRepository.findByRoleId(userCache.getRoleList().stream().map(SysRole::getId).toList()));
        }
    }


    public void resetRootPassword(ResetRootPassword data) {
        if (rootResetCode.equals(data.getCode())) {
            if (!StringUtils.checkPassword(data.getPassword())) {
                throw new GlobalException(ErrorCode.PARAMS_ERROR_PASSWORD_LOW);
            }
            SysUser user = userRepository.findByUsernameAndDeleted(rootAccount, 0);
            user.setPassword(passwordEncoder.encode(data.getPassword()));
            userRepository.save(user);
        } else {
            throw new GlobalException(ErrorCode.PARAMS_ERROR_RESET_CODE_REPEAT);
        }
    }
}
