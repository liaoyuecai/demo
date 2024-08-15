package com.demo.sys.service;

import com.demo.core.authentication.TokenManager;
import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.WebTreeNode;
import com.demo.core.exception.ErrorCode;
import com.demo.core.exception.GlobalException;
import com.demo.core.service.CURDService;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dao.*;
import com.demo.sys.datasource.dto.ResetPassword;
import com.demo.sys.datasource.dto.UserBindJobAndRole;
import com.demo.sys.datasource.entity.*;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service("userService")
@Transactional
public class UserService extends CURDService<SysUser, SysUserRepository> {

    @Resource
    private SysMenuRepository menuRepository;
    @Resource
    private SysRoleRepository roleRepository;
    @Resource
    private SysDepartmentRepository departmentRepository;
    @Resource
    private SysJobRepository jobRepository;
    @Resource
    private SysUserJobRepository userJobRepository;
    @Resource
    private SysUserRoleRepository userRoleRepository;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private TokenManager tokenManager;

    @Value("${user.default.password:'123456'}")
    private String defaultPassword;
    @Value("${user.default.avatar:''}")
    private String defaultAvatar;

    public UserService(@Autowired SysUserRepository repository) {
        super(repository);
    }

    /**
     * 完善当前用户缓存信息
     * 完善用户菜单、角色等
     *
     * @param authentication
     */
    public AuthUserCache currentUser(Authentication authentication) {
        //todo 后期移动到AuthUserService中
        AuthUserCache userCache = (AuthUserCache) authentication.getDetails();
        if (userCache.isRoot()) {
            List<SysMenu> menus = menuRepository.findByRoot();
            if (menus != null) userCache.setMenuList(menus);
        }
        if (userCache.getRoleList() != null && !userCache.getRoleList().isEmpty()) {
            List<SysMenu> menus = menuRepository.findByRoleId(userCache.getRoleList().stream().map(SysRole::getId).toList());
            if (menus != null) userCache.setMenuList(menus);
        }
        tokenManager.refreshToken(userCache.getToken(), authentication);
        return userCache;
    }

    @Override
    public SysUser save(ApiHttpRequest<SysUser> request) {
        SysUser entity = request.getData();
        if (entity == null) return null;
        if (entity.getId() == null) {
            SysUser user = repository.findByUsernameAndDeleted(entity.getUsername(), 0);
            if (user != null) {
                throw new GlobalException(ErrorCode.PARAMS_ERROR_USERNAME_REPEAT);
            }
            entity.setPassword(passwordEncoder.encode(defaultPassword));
            entity.setUserAvatar(defaultAvatar);
        } else {
            entity.setPassword(null);
            entity.setUserAvatar(null);
        }
        return super.save(request);
    }

    public List<WebTreeNode> findDeptAndJobs() {
        List<SysDepartment> departments = departmentRepository.findNotDeletedAndStatus();
        if (departments == null) return List.of();
        Map<Integer, WebTreeNode> nodeMap = new HashMap<>();
        List<WebTreeNode> rootList = new ArrayList<>();
        departments.forEach(dept -> {
            WebTreeNode node = new WebTreeNode();
            //为了解决ant-d tree显示问题 部门节点key-vlue替换
            String key = dept.getId() + dept.getDepartmentName();
            node.setKey(key);
            node.setParentKey(dept.getParentId());
            node.setTitle(dept.getDepartmentName());
            node.setValue(key);
            node.setDisabled(true);
            if (node.getParentKey() == null) rootList.add(node);
            nodeMap.put(dept.getId(), node);
        });
        nodeMap.values().forEach(node -> {
            if (node.getParentKey() != null) nodeMap.get(node.getParentKey()).addChild(node);
        });
        List<SysJob> jobs = jobRepository.findNotDeletedAndStatus();
        if (jobs != null) jobs.forEach(job -> {
            WebTreeNode node = new WebTreeNode();
            node.setKey("job-" + job.getId());
            node.setParentKey(job.getDeptId());
            node.setTitle(job.getJobName());
            node.setValue(job.getId());
            node.setLeaf(true);
            nodeMap.get(job.getDeptId()).addChild(node);
        });
        return rootList;
    }

    public List<SysRole> findRoles(AuthUserCache cache) {
        if (cache.isRoot()) return roleRepository.findNotDeletedAndStatus();
        return roleRepository.findByUser(cache.getId());
    }


    public void bindJobAndRole(ApiHttpRequest<UserBindJobAndRole> request) {
        if (!request.getUser().isRoot()) checkCreateId(request.getData().getUserId(), request.getUser().getId());
        userRoleRepository.deleteByUserId(request.getData().getUserId());
        userRoleRepository.flush();
        userRoleRepository.saveAll(request.getData().getRoleIds().stream().map(i -> new SysUserRole(request.getData().getUserId(), i)).toList());
        userJobRepository.deleteByUserId(request.getData().getUserId());
        userJobRepository.flush();
        userJobRepository.saveAll(request.getData().getRoleIds().stream().map(i -> new SysUserJob(request.getData().getUserId(), i)).toList());
    }

    public UserBindJobAndRole findBindJobAndRole(Integer userId) {
        UserBindJobAndRole bind = new UserBindJobAndRole();
        bind.setUserId(userId);
        bind.setJobIds(userJobRepository.findByUserId(userId).stream().map(SysUserJob::getJobId).toList());
        bind.setRoleIds(userRoleRepository.findByUserId(userId).stream().map(SysUserRole::getRoleId).toList());
        return bind;
    }

    /**
     * 此权限划分太复杂，比较适合大型的信息系统，小型系统一般这些都是同一个人操作，
     * 大型系统一般都有独有需求，包括角色、部门、岗位这些的设计和权限都会变更，所以在此不适用此函数
     * 根据用户id，查找部门+岗位 tree
     * 超管用户拥有完整的tree
     * 其他用户获取当前用户岗位，逆推到部门，从逆推的部门延申出tree（当逆推出的部门存在上下级关系时去重）
     * 此函数效率不高，如果部门与岗位树特别庞大时，建议牺牲体验不去重
     *
     * @param userCache
     * @return
     */
    @Deprecated
    public List<WebTreeNode> findDeptAndJobs(AuthUserCache userCache) {
        List<SysDepartment> departments = departmentRepository.findNotDeletedAndStatus();
        if (departments == null) return List.of();
        //节点map
        Map<Integer, WebTreeNode> nodeMap = new HashMap<>();
        List<WebTreeNode> rootList = new ArrayList<>();
        departments.forEach(dept -> {
            WebTreeNode node = new WebTreeNode();
            //为了解决ant-d tree显示问题 部门节点key-vlue替换
            String key = dept.getId() + dept.getDepartmentName();
            node.setKey(key);
            node.setParentKey(dept.getParentId());
            node.setTitle(dept.getDepartmentName());
            node.setValue(key);
            if (node.getParentKey() == null) rootList.add(node);
            nodeMap.put(dept.getId(), node);
        });
        nodeMap.values().forEach(node -> {
            if (node.getParentKey() != null) nodeMap.get(node.getParentKey()).addChild(node);
        });
        List<SysJob> jobs = jobRepository.findNotDeletedAndStatus();
        if (jobs != null) jobs.forEach(job -> {
            WebTreeNode node = new WebTreeNode();
            node.setKey("job-" + job.getId());
            node.setParentKey(job.getDeptId());
            node.setTitle(job.getJobName());
            node.setValue(job.getId());
            node.setLeaf(true);
            nodeMap.get(job.getDeptId()).addChild(node);
        });
        if (!userCache.isRoot()) {
            List<Integer> departmentIds = jobRepository.findDeptIdByUserId(userCache.getId());
            //记录子节点上所有父级及父级以上节点
            Map<Integer, Set<Integer>> childMap = new HashMap<>();
            List<WebTreeNode> nodes = new ArrayList<>();
            Set<Integer> rootDeptIds = new HashSet<>(departmentIds);
            departmentIds.forEach(id -> {
                boolean isAncestorOfAnotherRoot = false;
                for (Integer otherRootId : rootDeptIds) {
                    if (id != otherRootId && isAncestor(nodeMap, id, otherRootId)) {
                        isAncestorOfAnotherRoot = true;
                        break;
                    }
                }
                if (!isAncestorOfAnotherRoot) {
                    nodes.add(nodeMap.get(id));
                }
            });
            return nodes;
        }
        return rootList;
    }

    // 辅助方法来判断一个部门是否是另一个部门的祖先
    boolean isAncestor(Map<Integer, WebTreeNode> nodeMap, Integer descendantId, Integer ancestorId) {
        WebTreeNode descendantNode = nodeMap.get(descendantId);
        while (descendantNode != null && descendantNode.getParentKey() != null) {
            if (descendantNode.getParentKey().equals(ancestorId)) {
                return true;
            }
            descendantNode = nodeMap.get(descendantNode.getParentKey());
        }
        return false;
    }

    public void resetPassword(ResetPassword data, AuthUserCache userDetails) {
        Optional<SysUser> optional = repository.findById(userDetails.getId());
        if (!optional.isPresent()) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR_DATA_NOT_FOUND);
        }
        SysUser user = optional.get();
        if (!passwordEncoder.matches(data.getOldPassword(), user.getPassword())) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR_OLD_PWD);
        }
        user.setPassword(passwordEncoder.encode(data.getNewPassword()));
        user.setUpdateBy(user.getId());
        user.setUpdateTime(LocalDateTime.now());
        repository.save(user);
    }
}
