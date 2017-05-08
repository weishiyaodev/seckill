-- 数据库初始化脚本

-- 创建数据库
CREATE DATABASE seckill;
-- 使用数据库
use seckill;
-- 创建秒杀库存表
CREATE TABLE seckill(
	seckill_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
	name VARCHAR(120) NOT NULL COMMENT '商品名称',
	number INT NOT NULL COMMENT '库存数量',
	start_time TIMESTAMP NOT NULL COMMENT '秒杀开始时间',
	end_time TIMESTAMP NOT NULL COMMENT '秒杀结束时间',
	create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	PRIMARY KEY(seckill_id),
	key idx_start_time(start_time),
	key idx_end_time(end_time),
	key idx_create_time(create_time)
)ENGINE = InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

-- 初始化数据
insert into seckill(name, number, start_time, end_time)
values('200000 秒杀***', 1, '2017-4-24 11:10:00', '2017-4-25 00:00:00'),
('200000 秒杀飞机', 20, '2017-4-24 11:10:00', '2017-4-25 00:00:00'),
('10000 秒杀机器人', 20, '2017-4-23 00:00:00', '2017-4-25 00:00:00'),
('1000 秒杀 iphone6', 100, '2017-2-23 00:00:00', '2017-2-24 00:00:00'),
('500 秒杀 ipad2', 200, '2017-2-23 00:00:00', '2017-2-24 00:00:00'),
('300 秒杀 iphone6', 300, '2017-2-23 00:00:00', '2017-2-24 00:00:00'),
('200 秒杀 iphone6', 400, '2017-2-23 00:00:00', '2017-2-24 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证相关的信息
create table success_killed(
	seckill_id bigint not null comment '秒杀商品id',
	user_phone bigint not null comment '用户手机号',
	state tinyint not null default -1 comment '状态标识：-1：无效 0：成功 1：已付款 2：已发货',
	create_time timestamp not null comment '创建时间',
	primary key (seckill_id, user_phone), /* 联合主键 */ 
	key idx_create_time(create_time)
)ENGINE = InnoDB DEFAULT CHARSET=UTF8 COMMENT='秒杀成功明细表';