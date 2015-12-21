CREATE TABLE `sys_scheduler_task` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`bean_name` varchar(100) DEFAULT NULL,
`code` varchar(100) DEFAULT NULL,
`description` varchar(100) DEFAULT NULL,
`lifecycle` int(11),
`method_name` varchar(100) DEFAULT NULL,
`time_exp` varchar(50) DEFAULT NULL,
PRIMARY key (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;