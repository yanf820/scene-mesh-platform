package com.scene.mesh.foundation.impl.cache;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.scene.mesh.foundation.api.cache.ICache;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存实现
 * 使用Spring Framework的RedisTemplate实现Redis缓存功能
 */
public class RedisCache<K, V> implements ICache<K, V> {

    @Setter
    @Getter
    private String host;

    @Setter
    @Getter
    private int port;

    private RedisConnectionFactory connectionFactory;
    private RedisTemplate<K, V> redisTemplate;

    /**
     * 初始化Redis连接
     */
    public RedisCache(String host, int port) {
        this.host = host;
        this.port = port;

        // 创建Redis连接工厂
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        this.connectionFactory = new LettuceConnectionFactory(config);
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
    public boolean delete(K key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean exists(K key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean expire(K key, long expireSeconds) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS));
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

    /**
     * 自定义Redis序列化器，使用Java序列化
     */
    private static class CustomRedisSerializer<T> implements RedisSerializer<T> {
        @Override
        public byte[] serialize(T t) throws SerializationException {
            if (t == null) {
                return new byte[0];
            }

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(t);
                return bos.toByteArray();
            } catch (Exception e) {
                throw new SerializationException("Error serializing object", e);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public T deserialize(byte[] bytes) throws SerializationException {
            if (bytes == null || bytes.length == 0) {
                return null;
            }

            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                return (T) ois.readObject();
            } catch (Exception e) {
                throw new SerializationException("Error deserializing object", e);
            }
        }
    }
} 