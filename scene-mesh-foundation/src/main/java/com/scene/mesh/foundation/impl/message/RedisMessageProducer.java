// Redis Stream消息生产者 - 使用Spring Framework Redis
package com.scene.mesh.foundation.impl.message;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.scene.mesh.foundation.api.message.IMessageProducer;
import com.scene.mesh.foundation.api.message.IMessageSerializer;
import com.scene.mesh.foundation.api.message.MessageTopic;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用Spring Framework的StreamOperations实现的消息生产者
 */
public class RedisMessageProducer implements IMessageProducer {

    @Setter
    @Getter
    private String host;

    @Setter
    @Getter
    private int port;

    private IMessageSerializer serializer;
    private RedisConnectionFactory connectionFactory;
    private RedisTemplate<String, Object> redisTemplate;
    private StreamOperations<String, Object, Object> streamOperations;

    public RedisMessageProducer(String host, int port) {
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

        // 初始化 RedisTemplate
        redisTemplate.afterPropertiesSet();
        
        // 获取StreamOperations
        this.streamOperations = this.redisTemplate.opsForStream();
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
                // 序列化消息
                String data = SimpleObjectHelper.objectData2json(message);
                
                // 创建记录（字段-值映射）
                Map<String, Object> record = new HashMap<>();
                record.put("data", data);
                
                // 发布到Stream
                streamOperations.add(streamKey, record);
            } catch (Exception e) {
                throw new RuntimeException("Failed to send message to Redis Stream: " + e.getMessage(), e);
            }
        }
    }
}