package org.seckill.dao;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * ���� spring �� Junit ���ϣ�Junit ����ʱ���� spring IOC ����
 * @author wei.shiyao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
// ���� Junit spring �����ļ�
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

	// ע�� Dao ʵ��������
	@Resource
	private SeckillDao seckillDao;
	
	@Test
	public void testReduceNumber() throws Exception {
		int updateCount = seckillDao.reduceNumber(1000L, new Date());
		System.out.println(updateCount);
	}
	
	@Test
	public void testQueryById() throws Exception {
		long id = 1000;
		Seckill seckill = seckillDao.queryById(1000);
		System.out.println(seckill.getName());
		System.out.println(seckill);
	}
	
	@Test
	public void testQueryAll() throws Exception {
		List<Seckill> seckills = seckillDao.queryAll(0, 100);
		for (Seckill seckill : seckills) {
			System.out.println(seckill);
		}
	}
}
