DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `real_name`   varchar(20) NOT NULL COMMENT '真实名称',
    `username`    varchar(50) NOT NULL COMMENT '账户名',
    `user_avatar`    varchar(50) NOT NULL COMMENT '头像',
    `status`      smallint(1) DEFAULT 1 COMMENT '状态 0禁用1启用',
    `password`    varchar(200) NOT NULL COMMENT '密码',
    `email`       varchar(50) DEFAULT NULL COMMENT '电子邮件地址',
    `phone`       varchar(20) DEFAULT NULL COMMENT '电话号码',
    `create_time` datetime    DEFAULT NULL COMMENT '创建时间',
    `create_by`   int(11)     NOT NULL COMMENT '创建人',
    `update_time` datetime    DEFAULT NULL COMMENT '更新时间',
    `update_by`   int(11)   COMMENT '修改人',
    `deleted`     smallint(1) DEFAULT 0 COMMENT '删除状态',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT '系统用户表';

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`              int(11)     NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`       varchar(20) NOT NULL COMMENT '角色名称',
    `status`          smallint(1) DEFAULT 1 COMMENT '状态 0禁用1启用',
    `role_key`       varchar(20) DEFAULT NULL COMMENT '角色编码',
    `description`     varchar(50) DEFAULT NULL COMMENT '角色描述',
    `create_time`     datetime    DEFAULT NULL COMMENT '创建时间',
    `create_by`       int(11)     NOT NULL COMMENT '创建人',
    `update_time`     datetime    DEFAULT NULL COMMENT '更新时间',
    `update_by`   int(11)   COMMENT '修改人',
    `deleted`         smallint(1) DEFAULT 0 COMMENT '删除状态',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT '系统角色表';

DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`
(
    `id`              int(11)     NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `permission_name` varchar(50) NOT NULL COMMENT '权限名称',
    `status`          smallint(1) DEFAULT 1 COMMENT '状态 0禁用1启用',
    `permission_url`  varchar(50) NOT NULL COMMENT '权限路由',
    `description`     varchar(50) DEFAULT NULL COMMENT '权限描述',
    `permission_type` smallint(1) DEFAULT 1 COMMENT '权限类型：1菜单，2路由，3按钮',
    `create_time`     datetime    DEFAULT NULL COMMENT '创建时间',
    `create_by`       int(11)     NOT NULL COMMENT '创建人',
    `update_time`     datetime    DEFAULT NULL COMMENT '更新时间',
    `update_by`   int(11)   COMMENT '修改人',
    `deleted`         smallint(1) DEFAULT 0 COMMENT '删除状态',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT '系统权限表';

DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `id`      int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` int(11) NOT NULL COMMENT '用户ID',
    `role_id` int(11) NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    UNIQUE (`user_id`, `role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT '系统用户-角色表';

DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `role_id`       int(11) NOT NULL COMMENT '角色ID',
    `permission_id` int(11) NOT NULL COMMENT '权限ID',
    PRIMARY KEY (`id`),
    UNIQUE (`permission_id`, `role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT '系统角色-权限表';

DROP TABLE IF EXISTS `sys_user_department`;
CREATE TABLE `sys_user_department`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id`       int(11) NOT NULL COMMENT '角色ID',
    `department_id` int(11) NOT NULL COMMENT '部门ID',
    PRIMARY KEY (`id`),
    UNIQUE (`user_id`, `department_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT '系统角色-部门表';

DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department`
(
    `id`              int(11)               NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `department_name` varchar(50)           NOT NULL COMMENT '部门名称',
    `description`     varchar(50) COMMENT '部门描述',
    `parent_id`       int(11)     DEFAULT NULL COMMENT '上级部门id',
    `status`          smallint(1) DEFAULT 1 NOT NULL COMMENT '状态 0禁用1启用',
    `create_time`     datetime    DEFAULT NULL COMMENT '创建时间',
    `create_by`       int(11)               NOT NULL COMMENT '创建人',
    `update_time`     datetime    DEFAULT NULL COMMENT '更新时间',
    `update_by`   int(11)   COMMENT '修改人',
    `deleted`         smallint(1) DEFAULT 0 COMMENT '删除状态',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT '系统部门表';

DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`
(
    `id`              int(11)     NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `parent_id`         int(11)    COMMENT '上级id',
    `job_name`        varchar(50) NOT NULL COMMENT '岗位名称',
    `description`     varchar(50) COMMENT '岗位描述',
    `dept_id`         int(11)     NOT NULL COMMENT '部门id',
    `status`          smallint(1) DEFAULT 1 COMMENT '状态 0禁用1启用',
    `create_time`     datetime    DEFAULT NULL COMMENT '创建时间',
    `create_by`       int(11)     NOT NULL COMMENT '创建人',
    `update_time`     datetime    DEFAULT NULL COMMENT '更新时间',
    `update_by`   int(11)   COMMENT '修改人',
    `deleted`         smallint(1) DEFAULT 0 COMMENT '删除状态',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8  COMMENT '系统岗位表';

DROP TABLE IF EXISTS `sys_user_job`;
CREATE TABLE `sys_user_job`
(
    `id`      int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` int(11) NOT NULL COMMENT '角色ID',
    `job_id`  int(11) NOT NULL COMMENT '岗位ID',
    PRIMARY KEY (`id`),
    UNIQUE (`user_id`, `job_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8  COMMENT '系统用户-岗位表';

DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `id`          int(11)     NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `parent_id`   int(11)      DEFAULT NULL COMMENT '父级id',
    `status`      smallint(1)  DEFAULT 1 COMMENT '状态 0禁用1启用',
    `menu_sort`   smallint(3) NOT NULL COMMENT '排序',
    `menu_name`   varchar(50) NOT NULL COMMENT '菜单名称',
    `menu_key`    varchar(20) NOT NULL COMMENT '菜单编码',
    `menu_path`   varchar(100) DEFAULT NULL COMMENT '路由',
    `menu_icon`   varchar(50)  DEFAULT NULL COMMENT '图标',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `create_by`   int(11)     NOT NULL COMMENT '创建人',
    `update_time` datetime     DEFAULT NULL COMMENT '更新时间',
    `update_by`   int(11)   COMMENT '修改人',
    `deleted`     smallint(1)  DEFAULT 0 COMMENT '删除状态',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT '系统菜单表';

DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `id`      int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `role_id` int(11) NOT NULL COMMENT '角色ID',
    `menu_id` int(11) NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`id`),
    UNIQUE (`role_id`, `menu_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8   COMMENT '系统角色-菜单表';