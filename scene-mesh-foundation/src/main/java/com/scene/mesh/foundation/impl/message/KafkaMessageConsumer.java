package com.scene.mesh.foundation.impl.message;

import com.scene.mesh.foundation.api.message.IMessageConsumer;
import com.scene.mesh.foundation.api.message.IMessageSerializer;
import com.scene.mesh.foundation.api.message.MessageTopic;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

/**
 *
 */
public class KafkaMessageConsumer implements IMessageConsumer {

    private IMessageSerializer serializer;
    @Setter
    @Getter
    private String connectList;
    @Setter
    @Getter
    private String groupId;

    private KafkaConsumer<Long, byte[]> consumer;

    public void __init__() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.connectList);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "client-" + UUID.randomUUID());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

        this.consumer = new KafkaConsumer<>(properties);
    }

    @Override
    public void setSerializer(IMessageSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public <T> List<T> receive(MessageTopic topic, Class<T> messageType) {

        ConsumerRecords<Long, byte[]> records = consumer.poll(Duration.ofSeconds(1));
        if (records.isEmpty()) {
            return null;
        }
        List<T> msgs = new ArrayList<>();
        for (ConsumerRecord<Long, byte[]> record : records) {
            T msgObj = null;
            try {
                msgObj = this.serializer.deserialize(record.value(), messageType);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            msgs.add(msgObj);
        }
        return msgs;
    }

    public void shutdown() {
        if (consumer != null) {
            consumer.close();
        }
    }
}
