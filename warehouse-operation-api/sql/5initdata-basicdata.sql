--物理仓基础信息表
DROP TABLE IF EXISTS `t_bi_warehouse`;
CREATE TABLE `t_bi_warehouse` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(100) NOT NULL COMMENT '仓库编码',
  `name` varchar(100) NOT NULL COMMENT '仓库名称',
  `op_mode` int(11) DEFAULT NULL COMMENT '运营模式(待定)',
  `manage_mode` int(11) DEFAULT NULL COMMENT '管理模式(待定)',
  `pic` varchar(50) DEFAULT NULL COMMENT '联系人',
  `pic_contact` varchar(50) DEFAULT NULL COMMENT '联系人电话',
  `phone` varchar(50) DEFAULT NULL COMMENT '仓库电话',
  `fax` varchar(50) DEFAULT NULL COMMENT '仓库传真',
  `other_contact1` varchar(255) DEFAULT NULL COMMENT '其他联系人和方法1',
  `other_contact2` varchar(255) DEFAULT NULL COMMENT '其他联系人和方法2',
  `other_contact3` varchar(255) DEFAULT NULL COMMENT '其他联系人和方法3',
  `country_id` bigint(20) NOT NULL COMMENT '国家ID',
  `province_id` bigint(20) NOT NULL COMMENT '省ID',
  `city_id` bigint(20) NOT NULL COMMENT '市ID',
  `address` varchar(255) NOT NULL COMMENT '详细地址',
  `zip_code` varchar(20) NOT NULL COMMENT '邮政编码',
  `size` decimal(10,2) NOT NULL COMMENT '仓库面积(m²)',
  `size_uom` varchar(255) NOT NULL COMMENT '面积尺寸单位',
  `rent_price` decimal(10,2) DEFAULT '0.00' COMMENT '租金单价',
  `rent_price_uom` varchar(255) DEFAULT NULL COMMENT '货币单位',
  `description` varchar(255) DEFAULT NULL COMMENT '仓库描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人id',
  `lifecycle` int(11) DEFAULT '1' COMMENT '1.可用;2.已删除;0.禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--仓库区域基础信息表
DROP TABLE IF EXISTS `t_wh_area`;
CREATE TABLE `t_wh_area` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `wh_id` bigint(20) NOT NULL COMMENT '对应仓库ID',
  `area_code` varchar(100) NOT NULL COMMENT '区域编码',
  `area_name` varchar(100) NOT NULL COMMENT '区域名称',
  `area_type` varchar(255) NOT NULL COMMENT '区域类型',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `lifecycle` int(11) DEFAULT '1' COMMENT '1.可用;2.已删除;0.禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--单位基础信息表
DROP TABLE IF EXISTS `t_wh_uom`;
CREATE TABLE `t_wh_uom` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `uom_code` varchar(255) DEFAULT NULL COMMENT '单位编码',
  `uom_name` varchar(255) DEFAULT NULL COMMENT '单位名称',
  `conversion_rate` decimal(10,0) DEFAULT NULL COMMENT '换算率',
  `group_code` varchar(255) DEFAULT NULL COMMENT '所属单位类型编码',
  `sort_no` int(11) DEFAULT NULL COMMENT '排序号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `lifecycle` int(11) DEFAULT '1' COMMENT '1.可用;2.已删除;0.禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

--库位模板基础数据表
DROP TABLE IF EXISTS `t_wh_location_templet`;
CREATE TABLE `t_wh_location_templet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `wh_id` bigint(20) NOT NULL COMMENT '物理仓ID',
  `templet_code` varchar(255) NOT NULL COMMENT '库位模板编码',
  `templet_name` varchar(255) NOT NULL COMMENT '库位模板名称',
  `length` decimal(10,2) DEFAULT NULL COMMENT '长',
  `width` decimal(10,2) DEFAULT NULL COMMENT '宽',
  `high` decimal(10,2) DEFAULT NULL COMMENT '高',
  `length_uom` varchar(255) DEFAULT NULL COMMENT '长度单位',
  `volume` decimal(10,2) DEFAULT NULL COMMENT '体积',
  `volume_uom` varchar(255) DEFAULT NULL COMMENT '体积单位',
  `weight` decimal(10,2) DEFAULT NULL COMMENT '重量',
  `weight_uom` varchar(255) DEFAULT NULL COMMENT '重量单位',
  `size_type` varchar(255) DEFAULT NULL COMMENT '尺寸类别',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime DEFAULT NULL COMMENT '最后操作时间',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `lifecycle` int(11) DEFAULT '1' COMMENT '1.可用;2.已删除;0.禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--库位模板规则基础数据表
DROP TABLE IF EXISTS `t_wh_location_templet_rules`;
CREATE TABLE `t_wh_location_templet_rules` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `templet_id` bigint(20) NOT NULL COMMENT '模板ID',
  `dimension1` varchar(50) DEFAULT NULL COMMENT '维度1 A代表字母 N代表数字',
  `dimension2` varchar(50) DEFAULT NULL COMMENT '维度2 A代表字母 N代表数字',
  `dimension3` varchar(50) DEFAULT NULL COMMENT '维度2 A代表字母 N代表数字',
  `split_mark` varchar(50) DEFAULT NULL COMMENT '间隔符',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_modify_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `lifecycle` int(11) DEFAULT '1' COMMENT '1.可用;2.已删除;0.禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--国家省市区基础数据表
DROP TABLE IF EXISTS `t_bi_region`;
CREATE TABLE `t_bi_region` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `region_name` varchar(100) DEFAULT NULL COMMENT '区域名称',
  `region_name_en` varchar(255) DEFAULT NULL COMMENT '区域名称(英文)',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父栏目ID',
  `short_name` varchar(255) DEFAULT NULL COMMENT '区域简称',
  `sort_no` int(11) DEFAULT NULL COMMENT '排序号',
  `lifecycle` int(11) DEFAULT '1' COMMENT '1.可用;2.已删除;0.禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--单位基础数据
INSERT INTO t_wh_uom (uom_code,uom_name,group_code,sort_no,create_time,last_modify_time,lifecycle) VALUES 
('LENGTH_UOM','长度','UOM_TYPE',1,NOW(),NOW(),1);
INSERT INTO t_wh_uom (uom_code,uom_name,group_code,sort_no,create_time,last_modify_time,lifecycle) VALUES 
('AREA_UOM','面积','UOM_TYPE',2,NOW(),NOW(),1);
INSERT INTO t_wh_uom (uom_code,uom_name,group_code,sort_no,create_time,last_modify_time,lifecycle) VALUES 
('VOLUME_UOM','体积','UOM_TYPE',3,NOW(),NOW(),1);
INSERT INTO t_wh_uom (uom_code,uom_name,group_code,sort_no,create_time,last_modify_time,lifecycle) VALUES 
('WEIGHT_UOM','重量','UOM_TYPE',4,NOW(),NOW(),1);
INSERT INTO t_wh_uom (uom_code,uom_name,group_code,sort_no,create_time,last_modify_time,lifecycle) VALUES 
('CURRENCT_UOM','货币','UOM_TYPE',5,NOW(),NOW(),1);
