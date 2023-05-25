package com.haizhi.empower.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haizhi.empower.util.RedisUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 配置redis作为缓存管理器
 *
 * @author CristianWindy
 */
@Configuration
public class RedisConfig {

    @Value("${redis.host:172.17.0.1}")
    private String hostName;
    @Value("${redis.port:6379}")
    private int port;
    @Value("${redis.password:haizhi2018}")
    private String password;
    /**
     * 最大连接数, 默认8个
     */
    @Value("${redis.maxActive:10}")
    private int maxActive;
    /**
     * 最大空闲连接数, 默认8个
     */
    @Value("${redis.maxIdle:10}")
    private int maxIdle;
    /**
     * 最小空闲连接数, 默认0
     */
    @Value("${redis.minIdle:0}")
    private int minIdle;
    /**
     * 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
     */
    @Value("${redis.maxWait:30000}")
    private long maxWait;

    /**
     * 注入工具类
     *
     * @return
     */
    @Bean
    public RedisUtils defaultCache() {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(initRedisConnectionFactory(1));
        setSerializer(redisTemplate);
        redisTemplate.afterPropertiesSet();
        return new RedisUtils(redisTemplate);
    }

    /**
     * 设置key和value的序列化方式
     *
     * @param redisTemplate
     */
    private void setSerializer(RedisTemplate redisTemplate) {
        //value的序列化方式
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        //key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
    }

    /**
     * 初始化连接池属性
     *
     * @return
     */
    private JedisPoolConfig initJedisPoolConfig() {
        // 进行连接池属性设置
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWait);
        return poolConfig;
    }


    /**
     * 初始化连接参数
     *
     * @param bdIndex
     * @return
     */
    private RedisConnectionFactory initRedisConnectionFactory(int bdIndex) {
        JedisConnectionFactory jedisFactory = new JedisConnectionFactory(initJedisPoolConfig());
        jedisFactory.setHostName(hostName);
        jedisFactory.setPort(port);
        jedisFactory.setPassword(password);
        jedisFactory.setDatabase(bdIndex);
        jedisFactory.afterPropertiesSet(); // 初始化连接池配置
        return jedisFactory;
    }
}