package com.scene.mesh.foundation.impl.message;

import com.scene.mesh.foundation.api.message.IMessageProducer;
import com.scene.mesh.foundation.api.message.IMessageSerializer;
import com.scene.mesh.foundation.api.message.MessageTopic;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 */
public class KafkaMessageProducer implements IMessageProducer {

    private IMessageSerializer serializer;
    @Setter
    @Getter
    private String brokerList;
    private Producer<Long, byte[]> producer;
    private long messageKeyIndex;

    public void __init__() {

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.brokerList);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "client-" + UUID.randomUUID());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

        this.messageKeyIndex = 0L;
        this.producer = new KafkaProducer<>(properties);
    }

    @Override
    public void setSerializer(IMessageSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void send(MessageTopic topic, Object... messages) throws Exception {
        if (messages == null) return;
        for (Object message : messages) {
            byte[] messageBody = this.serializer.serialize(message);
            long key = this.messageKeyIndex++;
            if (this.messageKeyIndex >= Long.MAX_VALUE) this.messageKeyIndex = 0L;
            ProducerRecord<Long, byte[]> msg = new ProducerRecord<>(topic.getTopicName(), key, messageBody);
            this.producer.send(msg);
        }
    }

    /**
     * 关闭资源
     */
    public void shutdown() {
        if (producer != null) {
            producer.close();
        }
    }
}
