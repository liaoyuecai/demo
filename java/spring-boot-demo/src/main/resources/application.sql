/*
Target Server Type    : MYSQL
Target Server Version : 50740
File Encoding         : 65001

Date: 2024-08-19 10:29:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_department
-- ----------------------------
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `department_name` varchar(50) NOT NULL COMMENT '部门名称',
  `description` varchar(50) DEFAULT NULL COMMENT '部门描述',
  `parent_id` int(11) DEFAULT NULL COMMENT '上级部门id',
  `status` smallint(1) NOT NULL DEFAULT '1' COMMENT '状态 0禁用1启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` int(11) NOT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` int(11) DEFAULT NULL COMMENT '修改人',
  `deleted` smallint(1) DEFAULT '0' COMMENT '删除状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='系统部门表';

-- ----------------------------
-- Records of sys_department
-- ----------------------------
INSERT INTO `sys_department` VALUES ('6', '公司', null, null, '1', '2024-08-15 09:11:45', '1', null, null, '0');
INSERT INTO `sys_department` VALUES ('7', '财务部', null, '6', '1', '2024-08-15 09:11:58', '1', null, null, '0');
INSERT INTO `sys_department` VALUES ('8', '技术部', null, '6', '1', '2024-08-15 09:12:05', '1', null, null, '0');
INSERT INTO `sys_department` VALUES ('9', '销售部', null, null, '1', '2024-08-15 09:12:10', '1', null, null, '1');
INSERT INTO `sys_department` VALUES ('10', '销售部', null, '6', '1', '2024-08-15 09:12:23', '1', null, null, '0');
INSERT INTO `sys_department` VALUES ('11', 'XX部', null, '6', '1', '2024-08-15 09:12:30', '1', '2024-08-19 10:27:07', '0', '0');
INSERT INTO `sys_department` VALUES ('12', '技术部1', null, '8', '1', '2024-08-15 09:12:48', '1', '2024-08-19 10:27:14', '0', '0');
INSERT INTO `sys_department` VALUES ('13', '技术部2', null, '8', '1', '2024-08-15 09:12:56', '1', '2024-08-19 10:27:21', '0', '0');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `parent_id` int(11) DEFAULT NULL COMMENT '上级id',
  `job_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `description` varchar(50) DEFAULT NULL COMMENT '岗位描述',
  `dept_id` int(11) NOT NULL COMMENT '部门id',
  `status` smallint(1) DEFAULT '1' COMMENT '状态 0禁用1启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` int(11) NOT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` int(11) DEFAULT NULL COMMENT '修改人',
  `deleted` smallint(1) DEFAULT '0' COMMENT '删除状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='系统岗位表';

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES ('4', null, '系统管理员', null, '6', '1', '2024-08-15 09:54:07', '1', '2024-08-15 09:54:18', '1', '0');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `parent_id` int(11) DEFAULT NULL COMMENT '父级id',
  `status` smallint(1) DEFAULT '1' COMMENT '状态 0禁用1启用',
  `menu_sort` smallint(3) NOT NULL COMMENT '排序',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `menu_path` varchar(100) DEFAULT NULL COMMENT '路由',
  `menu_icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` int(11) NOT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` int(11) DEFAULT NULL COMMENT '修改人',
  `deleted` smallint(1) DEFAULT '0' COMMENT '删除状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='系统菜单表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', null, '1', '1', '系统管理', '/sys', 'RobotOutlined', '2024-08-05 14:29:57', '1', '2024-08-07 17:13:30', '1', '0');
INSERT INTO `sys_menu` VALUES ('2', '1', '1', '1', '菜单管理', '/sys/menu', 'AppstoreOutlined', '2024-08-05 15:46:17', '1', '2024-08-07 17:12:20', '1', '0');
INSERT INTO `sys_menu` VALUES ('3', '1', '1', '2', '用户管理', '/sys/user', 'UserOutlined', '2024-08-06 16:40:09', '1', '2024-08-07 17:12:07', '1', '0');
INSERT INTO `sys_menu` VALUES ('7', '1', '1', '3', '角色管理', '/sys/role', 'ContactsOutlined', '2024-08-07 17:10:15', '1', null, null, '0');
INSERT INTO `sys_menu` VALUES ('8', '1', '1', '5', '部门管理', '/sys/dept', 'ClusterOutlined', '2024-08-12 11:46:49', '1', null, null, '0');
INSERT INTO `sys_menu` VALUES ('9', '1', '1', '6', '岗位管理', '/sys/job', 'ContactsOutlined', '2024-08-12 15:19:56', '1', '2024-08-13 10:18:26', '1', '0');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(20) NOT NULL COMMENT '角色名称',
  `status` smallint(1) DEFAULT '1' COMMENT '状态 0禁用1启用',
  `role_key` varchar(20) DEFAULT NULL COMMENT '角色编码',
  `role_type` varchar(20) DEFAULT NULL COMMENT '角色类型（0 系统默认 1公开 2 不公开）',
  `description` varchar(50) DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` int(11) NOT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` int(11) DEFAULT NULL COMMENT '修改人',
  `deleted` smallint(1) DEFAULT '0' COMMENT '删除状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='系统角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', 'ces1', '1', 't1', '2', null, '2024-08-14 16:14:05', '1', null, null, '0');
INSERT INTO `sys_role` VALUES ('2', 't2', '1', 't2', '2', null, '2024-08-14 16:14:17', '1', null, null, '1');
INSERT INTO `sys_role` VALUES ('3', 't3', '1', 't3', '1', null, '2024-08-14 16:14:24', '1', null, null, '0');
INSERT INTO `sys_role` VALUES ('4', '管理员', '1', 'ADMIN', '2', null, '2024-08-15 09:06:52', '1', null, null, '0');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `menu_id` int(11) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_id` (`role_id`,`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8 COMMENT='系统角色-菜单表';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('11', '1', '1');
INSERT INTO `sys_role_menu` VALUES ('9', '1', '2');
INSERT INTO `sys_role_menu` VALUES ('10', '1', '3');
INSERT INTO `sys_role_menu` VALUES ('16', '2', '1');
INSERT INTO `sys_role_menu` VALUES ('17', '2', '2');
INSERT INTO `sys_role_menu` VALUES ('18', '2', '3');
INSERT INTO `sys_role_menu` VALUES ('19', '2', '7');
INSERT INTO `sys_role_menu` VALUES ('20', '2', '8');
INSERT INTO `sys_role_menu` VALUES ('21', '2', '9');
INSERT INTO `sys_role_menu` VALUES ('15', '3', '1');
INSERT INTO `sys_role_menu` VALUES ('12', '3', '3');
INSERT INTO `sys_role_menu` VALUES ('13', '3', '7');
INSERT INTO `sys_role_menu` VALUES ('14', '3', '9');
INSERT INTO `sys_role_menu` VALUES ('26', '4', '1');
INSERT INTO `sys_role_menu` VALUES ('22', '4', '3');
INSERT INTO `sys_role_menu` VALUES ('23', '4', '7');
INSERT INTO `sys_role_menu` VALUES ('24', '4', '8');
INSERT INTO `sys_role_menu` VALUES ('25', '4', '9');

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `permission_id` int(11) NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `permission_id` (`permission_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统角色-权限表';

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `real_name` varchar(20) NOT NULL COMMENT '真实名称',
  `username` varchar(50) NOT NULL COMMENT '账户名',
  `user_avatar` varchar(80) NOT NULL COMMENT '头像',
  `status` smallint(1) DEFAULT '1' COMMENT '状态 0禁用1启用',
  `password` varchar(200) NOT NULL COMMENT '密码',
  `email` varchar(50) DEFAULT NULL COMMENT '电子邮件地址',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话号码',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` int(11) NOT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` int(11) DEFAULT NULL COMMENT '修改人',
  `deleted` smallint(1) DEFAULT '0' COMMENT '删除状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='系统用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('0', '超级管理员', 'root', '/static/avatar/cat.png', '1', '$2a$10$MhP00UjpUdzbbVIQGCj/V.C2giC.P0X949/p2X/vV751DFjWOm7xS', '1111@163.com', '13344334433', '2024-08-05 10:11:46', '-1', '2024-08-16 16:16:44', '0', '0');

-- ----------------------------
-- Table structure for sys_user_department
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_department`;
CREATE TABLE `sys_user_department` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(11) NOT NULL COMMENT '角色ID',
  `department_id` int(11) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统角色-部门表';

-- ----------------------------
-- Records of sys_user_department
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_job`;
CREATE TABLE `sys_user_job` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(11) NOT NULL COMMENT '角色ID',
  `job_id` int(11) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='系统用户-岗位表';

-- ----------------------------
-- Records of sys_user_job
-- ----------------------------
INSERT INTO `sys_user_job` VALUES ('1', '1', '1');
INSERT INTO `sys_user_job` VALUES ('2', '1', '2');
INSERT INTO `sys_user_job` VALUES ('4', '3', '4');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 COMMENT='系统用户-角色表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('20', '1', '1');
INSERT INTO `sys_user_role` VALUES ('21', '1', '2');
INSERT INTO `sys_user_role` VALUES ('22', '3', '4');


DROP TABLE IF EXISTS `workflow_type`;
CREATE TABLE `workflow_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type_name` varchar(20) NOT NULL COMMENT '流程名称',
  `status` smallint(1) DEFAULT '1' COMMENT '状态 0禁用1启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` int(11) NOT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` int(11) DEFAULT NULL COMMENT '修改人',
  `deleted` smallint(1) DEFAULT '0' COMMENT '删除状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='流程类型表';

DROP TABLE IF EXISTS `workflow_record`;
CREATE TABLE `workflow_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
   `type_id` int(11) NOT NULL COMMENT '流程类型id',
  `workflow_name` varchar(20) NOT NULL COMMENT '流程名称',
  `workflow_nodes` text NOT NULL COMMENT '流程图形节点',
  `status` smallint(1) DEFAULT '1' COMMENT '状态 0禁用1启用',
  `workflow_status` smallint(1)  NOT NULL COMMENT '流程状态 0草稿 1发布',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` int(11) NOT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` int(11) DEFAULT NULL COMMENT '修改人',
  `deleted` smallint(1) DEFAULT '0' COMMENT '删除状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='流程表';


DROP TABLE IF EXISTS `workflow_node`;
CREATE TABLE `workflow_node` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workflow_id` int(11) NOT NULL   COMMENT '流程id',
  `parent_id` int(11)  COMMENT '上级节点',
  `child_workflow_id` int(11)  COMMENT '子流程id，当节点为子流程节点时生效',
  `is_return` int(11)  COMMENT '是否可以回退',
  `is_upload_file` int(11)  COMMENT '是否可以上传附件',
  `is_condition` int(1)  COMMENT '判断条件：当上级节点为决策节点时，字段值0为假，1为真',
  `node_name` varchar(20) NOT NULL COMMENT '节点名称',
  `node_type` smallint(2) NOT NULL COMMENT '节点类型 1开始，2结束，3任务节点，4子流程节点，5决策节点',
  `deleted` smallint(1) DEFAULT '0' COMMENT '删除状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='流程节点表';

DROP TABLE IF EXISTS `workflow_node_input`;
CREATE TABLE `workflow_node_input` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `node_id` int(11) NOT NULL   COMMENT '流程节点id',
  `input_necessary` int(1) NOT NULL COMMENT '是否必填：0否 1是',
  `input_title` varchar(20) NOT NULL COMMENT '输入名称',
  `input_type` smallint(2) NOT NULL COMMENT '输入类型 0 时间 1 input 2 text',
  `deleted` smallint(1) DEFAULT '0' COMMENT '删除状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='流程节点表';

DROP TABLE IF EXISTS `workflow_node_user`;
CREATE TABLE `workflow_node_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `node_id` int(11) NOT NULL COMMENT '节点id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='流程节点绑定用户表';

DROP TABLE IF EXISTS `workflow_node_cc_user`;
CREATE TABLE `workflow_node_cc_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `node_id` int(11) NOT NULL COMMENT '节点id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='流程节点绑定抄送用户表';

DROP TABLE IF EXISTS `workflow_node_job`;
CREATE TABLE `workflow_node_job` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `node_id` int(11) NOT NULL COMMENT '节点id',
  `job_id` int(11) NOT NULL COMMENT '岗位id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='流程节点绑定岗位表';

DROP TABLE IF EXISTS `workflow_active`;
CREATE TABLE `workflow_active` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workflow_name` varchar(200) NOT NULL COMMENT '流程名称',
  `node_id` int(11) NOT NULL COMMENT '目前流程节点',
  `parent_workflow_id` int(11)   COMMENT '流程id',
  `workflow_id` int(11) NOT NULL COMMENT '流程id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` int(11) NOT NULL COMMENT '创建人',
  `status` smallint(1) NOT NULL COMMENT '状态 0 结束 1正常',
  `update_by` int(11)   COMMENT '修改人，这里作为锁定流程使用，只有修改人能够修改流程，避免多人并发问题',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='流程活动表';

DROP TABLE IF EXISTS `workflow_distribute`;
CREATE TABLE `workflow_distribute` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workflow_id` int(11) NOT NULL COMMENT '流程id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='流程分发表';

DROP TABLE IF EXISTS `workflow_distribute_cc`;
CREATE TABLE `workflow_distribute_cc` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `workflow_id` int(11) NOT NULL COMMENT '流程id',
    `node_history_id` int(11) NOT NULL COMMENT '节点id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='流程抄送分发表';

DROP TABLE IF EXISTS `workflow_active_history`;
CREATE TABLE `workflow_active_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `parent_id` int(11)  COMMENT '上一级流转历史id',
  `node_id` int(11) NOT NULL COMMENT '目前流程节点',
  `active_input` text  COMMENT '流程输入内容',
  `active_file` varchar(100) COMMENT '附件地址',
  `status` smallint(1) NOT NULL COMMENT '状态 0 正在编辑 1 已经完成',
  `active_status` smallint(1) NOT NULL COMMENT '流程状态：1正常流转 2 回退 3审批通过 4审批不通过',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` int(11) NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='流程活动记录表';