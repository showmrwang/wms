-- ......................... 组织用户角色权限 ......................
-- 组织类型 
INSERT INTO au_operation_unit_type (ID, CODE, NAME, description, PARENT_OUT_ID)
VALUES (1, 'Root', '集团', NULL, NULL);

INSERT INTO au_operation_unit_type (ID, CODE, NAME, description, PARENT_OUT_ID)
VALUES (2, 'OperationCenter', '物流中心', 'DC', 1);

INSERT INTO au_operation_unit_type (ID, CODE, NAME, description, PARENT_OUT_ID)
VALUES (3, 'Warehouse', '仓库', NULL, 2);

-- 组织
INSERT INTO au_operation_unit (ID, CODE, NAME, FULL_NAME, lifecycle, OU_TYPE_ID, PARENT_OU_ID, OU_COMMENT, LAST_MODIFY_TIME)
VALUES (1, 'Root', '集团', '宝尊电商集团', 1, 1, NULL, NULL, NOW());

-- ......................... 数据字典 ......................
-- 是否
INSERT INTO sys_dictionary(dic_label,dic_value,description,lifecycle,group_name,lang,function_name,order_num) 
VALUES('是','yes',NULL,1,'yesOrNo','zh-cn',NULL,1);
INSERT INTO sys_dictionary(dic_label,dic_value,description,lifecycle,group_name,lang,function_name,order_num) 
VALUES('否','no',NULL,1,'yesOrNo','zh-cn',NULL,2);
-- 权限function
INSERT INTO sys_dictionary(dic_label,dic_value,description,lifecycle,group_name,lang,function_name,order_num) 
VALUES('查看','view','数据权限-查看',1,'PRIVILEGE_DATA',NULL,'PRIVILEGE',1);
INSERT INTO sys_dictionary(dic_label,dic_value,description,lifecycle,group_name,lang,function_name,order_num) 
VALUES('新增','add','数据权限-新增',1,'PRIVILEGE_DATA',NULL,'PRIVILEGE',2);
INSERT INTO sys_dictionary(dic_label,dic_value,description,lifecycle,group_name,lang,function_name,order_num) 
VALUES('修改','update','数据权限-修改',1,'PRIVILEGE_DATA',NULL,'PRIVILEGE',3);
INSERT INTO sys_dictionary(dic_label,dic_value,description,lifecycle,group_name,lang,function_name,order_num) 
VALUES('删除','remove','数据权限-删除',1,'PRIVILEGE_DATA',NULL,'PRIVILEGE',4);
INSERT INTO sys_dictionary(dic_label,dic_value,description,lifecycle,group_name,lang,function_name,order_num) 
VALUES('执行','operate','功能权限-执行',1,'PRIVILEGE_OP',NULL,'PRIVILEGE',1);
INSERT INTO sys_dictionary(dic_label,dic_value,description,lifecycle,group_name,lang,function_name,order_num) 
VALUES('配置','config','功能权限-配置',1,'PRIVILEGE_OP',NULL,'PRIVILEGE',2);
