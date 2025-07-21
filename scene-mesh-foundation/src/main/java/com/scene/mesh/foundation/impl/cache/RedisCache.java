package com.scene.mesh.foundation.impl.cache;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.scene.mesh.foundation.spec.cache.ICache;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存实现
 * 使用Spring Framework的RedisTemplate实现Redis缓存功能
 */
@Slf4j
public class RedisCache<K, V> implements ICache<K, V> {

    @Setter
    @Getter
    private String host;

    @Setter
    @Getter
    private int port;

    private final RedisConnectionFactory connectionFactory;
    private final RedisTemplate<K, V> redisTemplate;

    /**
     * 初始化Redis连接
     */
    public RedisCache(String host, int port) {
        this.host = host;
        this.port = port;

        // 配置连接池
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(20);          // 最大连接数
        poolConfig.setMaxIdle(10);           // 最大空闲连接
        poolConfig.setMinIdle(5);            // 最小空闲连接
        poolConfig.setMaxWaitMillis(2000);   // 获取连接最大等待时间

        // 创建Lettuce客户端配置
        LettucePoolingClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)
                .commandTimeout(Duration.ofSeconds(3))  // 命令超时
                .shutdownTimeout(Duration.ofSeconds(2)) // 关闭超时
                .build();

        // 创建Redis连接工厂
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        this.connectionFactory = new LettuceConnectionFactory(config,clientConfig);
        ((LettuceConnectionFactory) this.connectionFactory).afterPropertiesSet();

        // 创建RedisTemplate
        this.redisTemplate = new RedisTemplate<>();
        this.redisTemplate.setConnectionFactory(connectionFactory);

        // 配置完整的序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        redisTemplate.setDefaultSerializer(jsonSerializer);
        this.redisTemplate.afterPropertiesSet();
    }

    /**
     * 关闭资源
     */
    public void shutdown() {
        if (connectionFactory instanceof LettuceConnectionFactory) {
            ((LettuceConnectionFactory) connectionFactory).destroy();
        }
    }

    @Override
    public boolean set(K key, V value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean set(K key, V value, long expireSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public V get(K key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<V> getAll(K keyPrefix) {
        Set<K> keys = this.redisTemplate.keys(keyPrefix);
        if (!keys.isEmpty()) {
            return this.redisTemplate.opsForValue().multiGet(keys);
        }
        return null;
    }

    @Override
    public boolean delete(K key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteByKeyPrefix(K keyPrefix) {
        Set<K> keys = this.redisTemplate.keys(keyPrefix);
        if (!keys.isEmpty()) {
            Long deletedCount = redisTemplate.delete(keys);
            log.info("删除了 {} 下 {} 个 key " ,keyPrefix, deletedCount);
            return true;
        }
        return false;
    }

    @Override
    public boolean exists(K key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean expire(K key, long expireSeconds) {
        try {
            return redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean clear() {
        try {
            redisTemplate.getConnectionFactory().getConnection().flushAll();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
} 