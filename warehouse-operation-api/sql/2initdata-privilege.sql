-- - Root
-- -- 基础信息管理
-- ---- 组织配置
INSERT INTO au_privilege(acl,NAME,ou_type_id,TYPE,sort_no) VALUES ('ACL_ROOT_OU','组织配置',(SELECT id FROM au_operation_unit_type WHERE CODE = 'Root'),1,1);
-- ---- 物理仓配置
INSERT INTO au_privilege(acl,NAME,ou_type_id,TYPE,sort_no) VALUES ('ACL_ROOT_PWH','物理仓配置',(SELECT id FROM au_operation_unit_type WHERE CODE = 'Root'),1,2);
-- ---- 标准角色
INSERT INTO au_privilege(acl,NAME,ou_type_id,TYPE,sort_no) VALUES ('ACL_ROOT_ROLE','标准角色',(SELECT id FROM au_operation_unit_type WHERE CODE = 'Root'),1,3);
-- ---- 标准员工
INSERT INTO au_privilege(acl,NAME,ou_type_id,TYPE,sort_no) VALUES ('ACL_ROOT_USER','标准员工',(SELECT id FROM au_operation_unit_type WHERE CODE = 'Root'),1,4);
-- ---- 操作角色
INSERT INTO au_privilege(acl,NAME,ou_type_id,TYPE,sort_no) VALUES ('ACL_ROOT_LOROLE','操作角色',(SELECT id FROM au_operation_unit_type WHERE CODE = 'Root'),1,5);
-- ---- 操作员工
INSERT INTO au_privilege(acl,NAME,ou_type_id,TYPE,sort_no) VALUES ('ACL_ROOT_LOUSER','操作员工',(SELECT id FROM au_operation_unit_type WHERE CODE = 'Root'),1,6);

-- - OperationCenter
-- -- 设备
-- ---- 设备信息创建
INSERT INTO au_privilege(acl,NAME,ou_type_id,TYPE,sort_no) VALUES ('ACL_DC_DIVICE_ADDS','设备信息创建',(SELECT id FROM au_operation_unit_type WHERE CODE = 'OperationCenter'),1,1);
-- ---- 逻辑仓领用设备
INSERT INTO au_privilege(acl,NAME,ou_type_id,TYPE,sort_no) VALUES ('ACL_DC_DIVICE_USE','逻辑仓领用设备',(SELECT id FROM au_operation_unit_type WHERE CODE = 'OperationCenter'),1,2);
-- ---- 设备信息查询编辑
INSERT INTO au_privilege(acl,NAME,ou_type_id,TYPE,sort_no) VALUES ('ACL_DC_DIVICE','设备信息查询编辑',(SELECT id FROM au_operation_unit_type WHERE CODE = 'OperationCenter'),1,3);


-- urls
INSERT INTO `au_url` VALUES ('1', '/auth/std/role/list');
INSERT INTO `au_url` VALUES ('2', '/auth/std/role/add');
INSERT INTO `au_url` VALUES ('3', '/auth/std/role/copy');
INSERT INTO `au_url` VALUES ('4', '/auth/std/role/update');
INSERT INTO `au_url` VALUES ('5', '/auth/std/role/remove');
INSERT INTO `au_url` VALUES ('22', '/auth/oper/role/list');
INSERT INTO `au_url` VALUES ('23', '/auth/oper/role/add');
INSERT INTO `au_url` VALUES ('24', '/auth/oper/role/copy');
INSERT INTO `au_url` VALUES ('25', '/auth/oper/role/update');
INSERT INTO `au_url` VALUES ('26', '/auth/oper/role/remove');
INSERT INTO `au_url` VALUES ('27', '/auth/std/user/list');
INSERT INTO `au_url` VALUES ('28', '/auth/std/user/update');
INSERT INTO `au_url` VALUES ('29', '/auth/oper/user/list');
INSERT INTO `au_url` VALUES ('30', '/auth/oper/user/add');
INSERT INTO `au_url` VALUES ('31', '/auth/oper/user/update');

INSERT INTO `au_prifun_url` VALUES ('1', 'ACL_ROOT_ROLE', 'view', '1');
INSERT INTO `au_prifun_url` VALUES ('2', 'ACL_ROOT_ROLE', 'add', '2');
INSERT INTO `au_prifun_url` VALUES ('3', 'ACL_ROOT_ROLE', 'add', '3');
INSERT INTO `au_prifun_url` VALUES ('4', 'ACL_ROOT_ROLE', 'update', '4');
INSERT INTO `au_prifun_url` VALUES ('5', 'ACL_ROOT_ROLE', 'remove', '5');
INSERT INTO `au_prifun_url` VALUES ('6', 'ACL_ROOT_ROLE', null, '1');
INSERT INTO `au_prifun_url` VALUES ('27', 'ACL_ROOT_LOROLE', 'view', '22');
INSERT INTO `au_prifun_url` VALUES ('28', 'ACL_ROOT_LOROLE', 'add', '23');
INSERT INTO `au_prifun_url` VALUES ('29', 'ACL_ROOT_LOROLE', 'add', '24');
INSERT INTO `au_prifun_url` VALUES ('30', 'ACL_ROOT_LOROLE', 'update', '25');
INSERT INTO `au_prifun_url` VALUES ('31', 'ACL_ROOT_LOROLE', 'remove', '26');
INSERT INTO `au_prifun_url` VALUES ('32', 'ACL_ROOT_LOROLE', null, '22');
INSERT INTO `au_prifun_url` VALUES ('33', 'ACL_ROOT_USER', 'view', '27');
INSERT INTO `au_prifun_url` VALUES ('34', 'ACL_ROOT_USER', 'update', '28');
INSERT INTO `au_prifun_url` VALUES ('35', 'ACL_ROOT_USER', null, '27');
INSERT INTO `au_prifun_url` VALUES ('36', 'ACL_ROOT_LOUSER', null, '29');
INSERT INTO `au_prifun_url` VALUES ('37', 'ACL_ROOT_LOUSER', 'view', '29');
INSERT INTO `au_prifun_url` VALUES ('38', 'ACL_ROOT_LOUSER', 'add', '30');
INSERT INTO `au_prifun_url` VALUES ('39', 'ACL_ROOT_LOUSER', 'update', '31');