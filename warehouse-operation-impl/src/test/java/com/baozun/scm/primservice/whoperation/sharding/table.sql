CREATE TABLE `sales_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '所属店铺ID',
  `code` varchar(20) DEFAULT NULL COMMENT '编码',
  `price` decimal(10,4) DEFAULT NULL COMMENT '价格',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单信息' auto_increment=1;
