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
 * Redis 缓存访问层
 * 	暴露秒杀接口的时候，每一种商品只访问数据库一次，其余的暴露操作都是通过 redis
 * @author wei.shiyao
 *
 */
public class RedisDao {

	private JedisPool jedisPool;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	// 序列化
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
	
	public RedisDao(String ip, int port) {
		jedisPool = new JedisPool(ip, port);
	}
	
	/**
	 * 根据传过来的 seckillId，从 redis 中反序列化出对应的 Seckill 对象，并返回
	 * @param seckillId
	 * @return 根据 seckillId 反序列化得到的 Seckill
	 */
	public Seckill getSeckill(long seckillId) {
		// redis 操作的逻辑
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckillId;
				// redis 并没有实现内部序列化操作
				// get->byte[] -> 反序列化 -> Object（Seckill）
				// 采用了自定义序列化
				// protostuff : pojo
				byte[] bytes = jedis.get(key.getBytes());
				// 缓存重新获取
				if (bytes != null) {
					// 空对象：接收反序列化对象
					Seckill seckill = schema.newMessage();
					// seckill 被反序列化
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
	 * 把传递过来的 Seckill 序列化存入到 redis 中
	 * @param seckill
	 * @return
	 */
	public String putSeckill(Seckill seckill) {
		// set Object(Seckill) -> 序列化 -> byte[]
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckill.getSeckillId();
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				// 超时缓存
				int timeout = 60 * 60; // 1 小时
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