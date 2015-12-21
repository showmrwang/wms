-- 角色
INSERT INTO au_role(NAME,lifecycle,ou_type_id,is_system) 
VALUES ('系统管理员',1,(SELECT id FROM au_operation_unit_type WHERE CODE = 'Root'),1);

-- 角色权限
INSERT INTO au_role_privilege(role_id,acl,fun_code) 
SELECT 1,p.acl,s.dic_value FROM au_privilege p,sys_dictionary s WHERE s.group_name = 'PRIVILEGE_DATA' AND p.type = 1 AND p.ou_type_id = 1;

-- 用户
insert into `au_user` 
(`id`,`ou_id`, `login_name`, `user_name`, `PASSWORD`, `is_acc_non_expired`, `is_acc_non_locked`, `lifecycle`, `create_time`, `latest_update_time`, `latest_access_time`, `email`, `job_number`, `memo`) 
values(1,1,'admin','管理员','123456','1','1','1',now(),now(),now(),null,'admin',null);

-- 用户角色
INSERT INTO au_user_role(user_id,role_id,ou_id) 
VALUES (1,1,1);