// Redis Stream消息消费者 - 使用Spring Framework Redis
package com.scene.mesh.foundation.impl.message;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scene.mesh.foundation.api.message.IMessageConsumer;
import com.scene.mesh.foundation.api.message.IMessageSerializer;
import com.scene.mesh.foundation.api.message.MessageTopic;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 使用Spring Framework的StreamOperations实现的消息消费者
 */
@Slf4j
public class RedisMessageConsumer implements IMessageConsumer {

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

    @Setter
    private int batchSize = 10; // 默认一次获取10条消息

    @Setter
    private int timeoutSeconds = 1; // 默认超时1秒

    // 消费者组和消费者名称
    private String consumerGroup;
    private String consumerName;

    public void __init__() {
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

        // 为每个消费者实例生成唯一标识
        this.consumerGroup = "group-" + UUID.randomUUID().toString().substring(0, 8);
        this.consumerName = "consumer-" + UUID.randomUUID().toString().substring(0, 8);
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
    public <T> List<T> receive(MessageTopic topic, Class<T> messageType) {
        String streamKey = topic.getTopicName();
        List<T> messages = new ArrayList<>();

        try {
            try {
                // 创建消费者组
                redisTemplate.opsForStream().createGroup(streamKey, consumerGroup);
            }catch (Exception e){
                // 消费者组存在
            }
            // 从Stream读取消息
            List<MapRecord<String, Object, Object>> streamRecords = streamOperations.read(
                    Consumer.from(consumerGroup, consumerName),
                    StreamReadOptions.empty()
                            .count(batchSize)
                            .block(Duration.ofSeconds(timeoutSeconds)),
                    StreamOffset.create(streamKey, ReadOffset.lastConsumed())
            );

            if (streamRecords != null && !streamRecords.isEmpty()) {
                for (MapRecord<String, Object, Object> record : streamRecords) {
                    // 获取消息内容
                    Map<Object, Object> body = record.getValue();

                    // 获取"data"字段的值
                    if (body.containsKey("data")) {
                        String data = (String) body.get("data");

                        // 反序列化消息
                        T msg = SimpleObjectHelper.str2Obj(data, messageType);
                        messages.add(msg);
                    }

                    // 确认消息已处理(ACK)
                    streamOperations.acknowledge(consumerGroup, record);
                }
            }

            return messages.isEmpty() ? null : messages;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}