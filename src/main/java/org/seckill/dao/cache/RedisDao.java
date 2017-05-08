package org.seckill.dao.cache;

import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis ������ʲ�
 * 	��¶��ɱ�ӿڵ�ʱ��ÿһ����Ʒֻ�������ݿ�һ�Σ�����ı�¶��������ͨ�� redis
 * @author wei.shiyao
 *
 */
public class RedisDao {

	private JedisPool jedisPool;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	// ���л�
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
	
	public RedisDao(String ip, int port) {
		jedisPool = new JedisPool(ip, port);
	}
	
	/**
	 * ���ݴ������� seckillId���� redis �з����л�����Ӧ�� Seckill ���󣬲�����
	 * @param seckillId
	 * @return ���� seckillId �����л��õ��� Seckill
	 */
	public Seckill getSeckill(long seckillId) {
		// redis �������߼�
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckillId;
				// redis ��û��ʵ���ڲ����л�����
				// get->byte[] -> �����л� -> Object��Seckill��
				// �������Զ������л�
				// protostuff : pojo
				byte[] bytes = jedis.get(key.getBytes());
				// �������»�ȡ
				if (bytes != null) {
					// �ն��󣺽��շ����л�����
					Seckill seckill = schema.newMessage();
					// seckill �������л�
					ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
					return seckill;
				}
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
		return null;
	}
	
	/**
	 * �Ѵ��ݹ����� Seckill ���л����뵽 redis ��
	 * @param seckill
	 * @return
	 */
	public String putSeckill(Seckill seckill) {
		// set Object(Seckill) -> ���л� -> byte[]
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckill.getSeckillId();
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				// ��ʱ����
				int timeout = 60 * 60; // 1 Сʱ
				String result = jedis.setex(key.getBytes(), timeout, bytes);
				return result;
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
}