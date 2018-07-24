package com.roya.service;

import com.google.common.base.Joiner;
import com.roya.beans.CacheKeyConstans;
import com.roya.utils.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;

/**
 * @author Loyaill
 * @description :
 * @CreateTime 2018-07-24-16:58
 */
@Service
@Slf4j
public class SysCacheService {

	@Resource(name = "redisPool")
	private RedisPool redisPool;

	/**
	 * 保存cache的方法
	 */
	public void saveCache(String toSaveValue, int timeoutSeconds , CacheKeyConstans prefix){
		saveCache(toSaveValue,timeoutSeconds,prefix,null);
	}

	/**
	 * 保存cache的方法 很多值的时候
	 */
	public void saveCache(String toSaveValue, int timeoutSeconds , CacheKeyConstans prefix, String... keys){
		//保存的值
		if(toSaveValue == null){
			return;
		}
		ShardedJedis shardedJedis = null;
		try {
			String cacheKey = generateCacheKey(prefix, keys);
			shardedJedis = redisPool.instance(); //获取缓存连接实例
			shardedJedis.setex(cacheKey,timeoutSeconds,toSaveValue); //设置待过期方法
		}catch (Exception e){
			log.error("save cache exception , prefix:{},keys:{}",prefix.name(),JsonMapper.obj2String(keys),e);
		}finally {
			redisPool.safeClose(shardedJedis);
		}
	}

	/**
	 * 自动拼接cache键
	 * @param prefix 前缀
	 * @param keys 区别
	 * @return 完整键
	 */
	public String generateCacheKey(CacheKeyConstans prefix, String... keys){
		String key = prefix.name();
		if (keys != null && keys.length > 0){
			key += "_"+ Joiner.on("_").join(keys);
		}
		return key;
	}


	/**
	 * 获取cache的功能
	 */
	public String getFromCache(CacheKeyConstans prefix, String... keys){
		ShardedJedis shardedJedis = null;
		String cacheKey = generateCacheKey(prefix,keys);
		try{
			shardedJedis = redisPool.instance();
			String value = shardedJedis.get(cacheKey);
			return value;
		}catch (Exception e){
			log.error("get from cache exception , prefix:{},keys:{}",prefix.name(),JsonMapper.obj2String(keys),e);
			return null;
		}finally {
			redisPool.safeClose(shardedJedis);
		}
	}


}
