-- ���ݿ��ʼ���ű�

-- �������ݿ�
CREATE DATABASE seckill;
-- ʹ�����ݿ�
use seckill;
-- ������ɱ����
CREATE TABLE seckill(
	seckill_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '��Ʒ���id',
	name VARCHAR(120) NOT NULL COMMENT '��Ʒ����',
	number INT NOT NULL COMMENT '�������',
	start_time TIMESTAMP NOT NULL COMMENT '��ɱ��ʼʱ��',
	end_time TIMESTAMP NOT NULL COMMENT '��ɱ����ʱ��',
	create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '����ʱ��',
	PRIMARY KEY(seckill_id),
	key idx_start_time(start_time),
	key idx_end_time(end_time),
	key idx_create_time(create_time)
)ENGINE = InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='��ɱ����';

-- ��ʼ������
insert into seckill(name, number, start_time, end_time)
values('200000 ��ɱ***', 1, '2017-4-24 11:10:00', '2017-4-25 00:00:00'),
('200000 ��ɱ�ɻ�', 20, '2017-4-24 11:10:00', '2017-4-25 00:00:00'),
('10000 ��ɱ������', 20, '2017-4-23 00:00:00', '2017-4-25 00:00:00'),
('1000 ��ɱ iphone6', 100, '2017-2-23 00:00:00', '2017-2-24 00:00:00'),
('500 ��ɱ ipad2', 200, '2017-2-23 00:00:00', '2017-2-24 00:00:00'),
('300 ��ɱ iphone6', 300, '2017-2-23 00:00:00', '2017-2-24 00:00:00'),
('200 ��ɱ iphone6', 400, '2017-2-23 00:00:00', '2017-2-24 00:00:00');

-- ��ɱ�ɹ���ϸ��
-- �û���¼��֤��ص���Ϣ
create table success_killed(
	seckill_id bigint not null comment '��ɱ��Ʒid',
	user_phone bigint not null comment '�û��ֻ���',
	state tinyint not null default -1 comment '״̬��ʶ��-1����Ч 0���ɹ� 1���Ѹ��� 2���ѷ���',
	create_time timestamp not null comment '����ʱ��',
	primary key (seckill_id, user_phone), /* �������� */ 
	key idx_create_time(create_time)
)ENGINE = InnoDB DEFAULT CHARSET=UTF8 COMMENT='��ɱ�ɹ���ϸ��';