// Redis Stream消息生产者 - 使用Spring Framework Redis
package com.scene.mesh.foundation.impl.message;

import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.foundation.spec.message.IMessageProducer;
import com.scene.mesh.foundation.spec.message.IMessageSerializer;
import com.scene.mesh.foundation.spec.message.MessageTopic;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class RedisMessageProducer implements IMessageProducer {

    @Setter
    @Getter
    private String host;

    @Setter
    @Getter
    private int port;

    private IMessageSerializer serializer;
    private RedisConnectionFactory connectionFactory;
    private StreamOperations<String, Object, Object> streamOperations;

    public void __init__(){

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

        // create redis factory
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        this.connectionFactory = new LettuceConnectionFactory(config,clientConfig);
        ((LettuceConnectionFactory) this.connectionFactory).afterPropertiesSet();

        // create redis template
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        redisTemplate.setDefaultSerializer(jsonSerializer);

        // init RedisTemplate
        redisTemplate.afterPropertiesSet();

        // get StreamOperations
        this.streamOperations = redisTemplate.opsForStream();
    }

    public void shutdown() {
        if (connectionFactory instanceof LettuceConnectionFactory) {
            ((LettuceConnectionFactory) connectionFactory).destroy();
        }
    }

    @Override
    public void setSerializer(IMessageSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void send(MessageTopic topic, Object... messages) throws Exception {
        if (messages == null || messages.length == 0) {
            return;
        }

        String streamKey = topic.getTopicName();

        for (Object message : messages) {
            try {
                Map<String, Object> record = new HashMap<>();
                record.put("data", this.serializer.serialize(message));

                streamOperations.add(streamKey, record);
            } catch (Exception e) {
                throw new RuntimeException("Failed to send message to Redis Stream: " + e.getMessage(), e);
            }
        }
    }
}