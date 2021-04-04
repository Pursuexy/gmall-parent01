package com.itcast.gmall.pms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * 自定义序列化器
 */
@Configuration
public class PmsRedisConfig {

	// @Bean
	// public RedisSerializer redisSerializer() {
	// 	return new GenericJackson2JsonRedisSerializer();
	// 	// return new Jackson2JsonRedisSerializer<Admin>();//支持泛型的自定义redis序列化器
	// }

	@Bean("redisTemplate")
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		//修改默认
		redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
		return redisTemplate;
	}

}
