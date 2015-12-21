-- ......................... 系统组织用户角色权限 ......................
-- 1组织类型
CREATE TABLE AU_OPERATION_UNIT_TYPE
(
  id            BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  CODE          VARCHAR(32) COMMENT '组织类型简称/编码',
  NAME          VARCHAR(32) COMMENT '组织类型名称',
  description   VARCHAR(64) COMMENT '组织类型描述',
  parent_out_id BIGINT(20) COMMENT '父级组织类型'
);

-- 2组织
CREATE TABLE AU_OPERATION_UNIT
(
  id               BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  CODE             VARCHAR(64) COMMENT '组织编码',
  NAME             VARCHAR(128) COMMENT '组织名称',
  full_name        VARCHAR(128) COMMENT '组织全称',
  lifecycle        INT(11) COMMENT '1.可用;2.已删除;0.禁用',
  ou_type_id       BIGINT(20) COMMENT '组织类型',
  parent_ou_id     BIGINT(20) COMMENT '父组织',
  ou_comment       VARCHAR(64) COMMENT '备注',
  last_modify_time DATETIME COMMENT '最后修改时间'
);

-- 3用户
CREATE TABLE AU_USER
(
  id                 BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  ou_id              BIGINT(20) COMMENT '所属组织',
  login_name         VARCHAR(128) COMMENT '登录名',
  user_name          VARCHAR(128) COMMENT '用户姓名',
  PASSWORD           VARCHAR(256) COMMENT '密码',
  is_acc_non_expired TINYINT(1) COMMENT '用户帐号是否未过期，过期帐号无法登录系统',
  is_acc_non_locked  TINYINT(1) COMMENT '用户帐号是否未被锁定，被锁定的用户无法使用系统',
  lifecycle          INT(11) COMMENT '1.可用;2.已删除;0.禁用',
  create_time        DATETIME COMMENT '创建时间',
  latest_update_time DATETIME COMMENT '最后修改时间',
  latest_access_time DATETIME COMMENT '最后登录时间',
  email              VARCHAR(256) COMMENT '邮箱',
  job_number         VARCHAR(32) COMMENT '工号',
  memo               VARCHAR(128) COMMENT '备注'
);

-- 4角色
CREATE TABLE AU_ROLE
(
  id           BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  NAME         VARCHAR(128) COMMENT '角色名称',
  lifecycle    INT(11) COMMENT '1.可用;2.已删除;0.禁用',
  ou_type_id   BIGINT(20) COMMENT '组织类型',
  is_system    TINYINT(1) DEFAULT 0 COMMENT '是否是系统初始化的角色'
);

-- 5权限
CREATE TABLE AU_PRIVILEGE
(
  id          BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  acl         VARCHAR(200) NOT NULL UNIQUE COMMENT 'ACL(唯一)',
  NAME        VARCHAR(128) COMMENT '权限名称',
  ou_type_id  BIGINT(20) COMMENT '组织类型',
  TYPE 	      INT(11) COMMENT '1.数据权限;2.功能权限',
  GROUP_NAME  VARCHAR(50) COMMENT '分组名称',
  sort_no        INT(11) COMMENT '序号'
);

-- 6 菜单
CREATE TABLE AU_MENU_ITEM
(
  id             BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  acl         	 VARCHAR(200) COMMENT '权限ACL',
  NAME           VARCHAR(64) COMMENT '菜单名称',
  url      	 	 VARCHAR(200) COMMENT '入口URL',
  sort_no        INT(11) COMMENT '序号，仅对同级菜单排序用',
  is_group       TINYINT(1) COMMENT '是否有子菜单(菜单组)',
  parent_id 	 BIGINT(20) COMMENT '父菜单'
);

--  7 角色权限关联表
create table au_role_privilege
(
   id                   bigint(20) not null auto_increment comment '主键',
   role_id              bigint(20) not null comment '角色ID',
   acl                  varchar(200) not null comment '权限的ACL',
   fun_code             varchar(500) comment '权限功能的CODE，数据来源于sys_dictionary的dic_value',
   primary key (id)
);

alter table au_role_privilege comment '角色权限关联表';

-- 8 用户角色组织关联表
CREATE TABLE AU_USER_ROLE
(
  id            BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  user_id       BIGINT(20) NOT NULL COMMENT '用户ID',
  role_id       BIGINT(20) NOT NULL COMMENT '角色ID',
  ou_id       	BIGINT(20) COMMENT '组织ID'
);

-- 9 受管控URL
create table au_url
(
   id                   bigint(20) not null auto_increment comment '主键',
   url                  varchar(200) comment '受管控URL',
   primary key (id)
);

alter table au_url comment '受管控URL';

-- 10 URL与权限功能关联表
create table au_prifun_url
(
   id                   int not null auto_increment comment '主键',
   acl                  varchar(200) comment '权限ACL',
   fun_code             varchar(500) comment '权限功能CODE',
   url_id               bigint(20) comment 'URL的ID',
   primary key (id)
);

alter table au_prifun_url comment 'URL与权限功能关联表';

-- ......................... 本地组织用户角色权限 ......................
-- 1 本地组织类型
CREATE TABLE LO_OPERATION_UNIT_TYPE
(
  id            BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  CODE          VARCHAR(32) COMMENT '组织类型简称/编码',
  NAME          VARCHAR(32) COMMENT '组织类型名称',
  description   VARCHAR(64) COMMENT '组织类型描述',
  parent_out_id BIGINT(20) COMMENT '父级组织类型'
);

-- 2 本地组织
CREATE TABLE LO_OPERATION_UNIT
(
  id               BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  CODE             VARCHAR(64) COMMENT '组织编码',
  NAME             VARCHAR(128) COMMENT '组织名称',
  full_name        VARCHAR(128) COMMENT '组织全称',
  lifecycle        INT(11) COMMENT '1.可用;2.已删除;0.禁用',
  ou_type_id       BIGINT(20) COMMENT '组织类型',
  parent_ou_id     BIGINT(20) COMMENT '父组织',
  ou_comment       VARCHAR(64) COMMENT '备注',
  last_modify_time DATETIME COMMENT '最后修改时间'
);

-- 3 本地用户
CREATE TABLE LO_USER
(
  id                 BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  ou_id              BIGINT(20) COMMENT '所属组织',
  login_name         VARCHAR(128) COMMENT '登录名',
  user_name          VARCHAR(128) COMMENT '用户姓名',
  PASSWORD           VARCHAR(256) COMMENT '密码',
  is_acc_non_expired TINYINT(1) COMMENT '用户帐号是否未过期，过期帐号无法登录系统',
  is_acc_non_locked  TINYINT(1) COMMENT '用户帐号是否未被锁定，被锁定的用户无法使用系统',
  lifecycle          INT(11) COMMENT '1.可用;2.已删除;0.禁用',
  create_time        DATETIME COMMENT '创建时间',
  latest_update_time DATETIME COMMENT '最后修改时间',
  latest_access_time DATETIME COMMENT '最后登录时间',
  email              VARCHAR(256) COMMENT '邮箱',
  job_number         VARCHAR(32) COMMENT '工号',
  memo               VARCHAR(128) COMMENT '备注'
);

-- 4 本地角色
CREATE TABLE LO_ROLE
(
  id           BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  NAME         VARCHAR(128) COMMENT '角色名称',
  lifecycle    INT(11) COMMENT '1.可用;2.已删除;0.禁用',
  ou_type_id   BIGINT(20) COMMENT '组织类型'
);

-- 5 本地权限
CREATE TABLE LO_PRIVILEGE
(
  id          BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  acl         VARCHAR(200) NOT NULL UNIQUE COMMENT 'ACL(唯一)',
  NAME        VARCHAR(128) COMMENT '权限名称',
  ou_type_id  BIGINT(20) COMMENT '组织类型',
  TYPE 	      INT(11) COMMENT '1.数据权限;2.功能权限',
  GROUP_NAME  VARCHAR(50) COMMENT '分组名称',
  sort_no        INT(11) COMMENT '序号'
);

--  6 本地角色权限关联表
CREATE TABLE LO_ROLE_PRIVILEGE
(
   id                   bigint(20) not null auto_increment comment '主键',
   role_id              bigint(20) not null comment '角色ID',
   acl                  varchar(200) not null comment '权限的ACL',
   fun_code             varchar(500) comment '权限功能的CODE，数据来源于sys_dictionary的dic_value',
   primary key (id)
);

--  7 本地用户角色组织关联表
CREATE TABLE LO_USER_ROLE
(
  id            BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  user_id       BIGINT(20) NOT NULL COMMENT '用户ID',
  role_id       BIGINT(20) NOT NULL COMMENT '角色ID',
  ou_id       	BIGINT(20) COMMENT '组织ID'
);

-- 8 本地受管控URL
create table LO_URL
(
   id                   bigint(20) not null auto_increment comment '主键',
   url                  varchar(200) comment '受管控URL',
   primary key (id)
);

-- 9 权限和URL关联表
CREATE TABLE LO_PRIFUN_URL
(
   id                   int not null auto_increment comment '主键',
   acl                  varchar(200) comment '权限ACL',
   fun_code             varchar(500) comment '权限功能CODE',
   url_id               bigint(20) comment 'URL的ID',
   primary key (id)
);

-- ......................... 系统相关 ......................
-- 定时任务表
CREATE TABLE `sys_scheduler_task` (
`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
`bean_name` VARCHAR(100) DEFAULT NULL,
`code` VARCHAR(100) DEFAULT NULL,
`description` VARCHAR(100) DEFAULT NULL,
`lifecycle` INT(11),
`method_name` VARCHAR(100) DEFAULT NULL,
`time_exp` VARCHAR(50) DEFAULT NULL,
`args` VARCHAR(255) DEFAULT NULL,
PRIMARY KEY (`id`)
);

-- 数据字典
CREATE TABLE `sys_dictionary` (
`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
`dic_label` VARCHAR(500) DEFAULT NULL COMMENT '显示名称',
`dic_value` VARCHAR(500) DEFAULT NULL COMMENT '数据值',
`description` VARCHAR(100) DEFAULT NULL COMMENT '描述',
`lifecycle` INT(11) COMMENT '生命周期',
`group_name` VARCHAR(100) DEFAULT NULL COMMENT '分组',
`function_name` VARCHAR(100) DEFAULT NULL COMMENT '功能(高于分组，将不同的功能进行区分，可以为null)',
`order_num` INT(11) COMMENT '排序号',
`lang` VARCHAR(50) DEFAULT NULL COMMENT '语言',
PRIMARY KEY (`id`)
);

-- 序列号计数器
CREATE TABLE sys_sequence_counter
(
   id                   BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
   category             VARCHAR(30) COMMENT '分组',
   CODE                 VARCHAR(100) COMMENT '关键key',
   counter              INT(11) COMMENT '计数',
   VERSION              INT(11) COMMENT '版本控制',
   PRIMARY KEY (id)
);

ALTER TABLE sys_sequence_counter COMMENT '序列号计数器';
