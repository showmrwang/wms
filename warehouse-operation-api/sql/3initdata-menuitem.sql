-- -------   menus ------------------
-- 基础信息管理
INSERT INTO au_menu_item(acl,NAME,url,sort_no,is_group,parent_id) VALUES (NULL,'基础信息管理',NULL,1,TRUE,NULL);
INSERT INTO au_menu_item(acl,NAME,url,sort_no,is_group,parent_id) VALUES ('ACL_ROOT_OU','组织配置','/auth/std/ou/list',1,FALSE,(SELECT m.id FROM au_menu_item m WHERE m.NAME = '基础信息管理'));
INSERT INTO au_menu_item(acl,NAME,url,sort_no,is_group,parent_id) VALUES ('ACL_ROOT_PWH','物理仓配置','/bi/pwh/list',2,FALSE,(SELECT m.id FROM au_menu_item m WHERE m.NAME = '基础信息管理'));
INSERT INTO au_menu_item(acl,NAME,url,sort_no,is_group,parent_id) VALUES ('ACL_ROOT_ROLE','标准角色','/auth/std/role/list',3,FALSE,(SELECT m.id FROM au_menu_item m WHERE m.NAME = '基础信息管理'));
INSERT INTO au_menu_item(acl,NAME,url,sort_no,is_group,parent_id) VALUES ('ACL_ROOT_USER','标准员工','/auth/std/user/list',4,FALSE,(SELECT m.id FROM au_menu_item m WHERE m.NAME = '基础信息管理'));
INSERT INTO au_menu_item(acl,NAME,url,sort_no,is_group,parent_id) VALUES ('ACL_ROOT_LOROLE','操作角色','/auth/oper/role/list',5,FALSE,(SELECT m.id FROM au_menu_item m WHERE m.NAME = '基础信息管理'));
INSERT INTO au_menu_item(acl,NAME,url,sort_no,is_group,parent_id) VALUES ('ACL_ROOT_LOUSER','操作员工','/auth/oper/user/list',6,FALSE,(SELECT m.id FROM au_menu_item m WHERE m.NAME = '基础信息管理'));

-- 设备
INSERT INTO au_menu_item(NAME,sort_no,is_group) VALUES ('设备',1,TRUE);
INSERT INTO au_menu_item(acl,NAME,url,sort_no,is_group,parent_id) 
 VALUES ('ACL_DC_DIVICE_ADDS','设备信息创建','/bi/device/adds',1,FALSE,(SELECT m.id FROM au_menu_item m WHERE m.NAME = '设备'));
INSERT INTO au_menu_item(acl,NAME,url,sort_no,is_group,parent_id) 
 VALUES ('ACL_DC_DIVICE_USE','逻辑仓领用设备','/bi/device/use',2,FALSE,(SELECT m.id FROM au_menu_item m WHERE m.NAME = '设备'));
INSERT INTO au_menu_item(acl,NAME,url,sort_no,is_group,parent_id) 
 VALUES ('ACL_DC_DIVICE','设备信息查询编辑','/bi/device/list',3,FALSE,(SELECT m.id FROM au_menu_item m WHERE m.NAME = '设备'));
