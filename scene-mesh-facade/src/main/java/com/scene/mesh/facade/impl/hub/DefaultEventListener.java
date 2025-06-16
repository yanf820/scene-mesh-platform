package com.scene.mesh.facade.impl.hub;

import com.scene.mesh.facade.api.ProtocolType;
import com.scene.mesh.facade.api.hub.IEventListener;
import com.scene.mesh.facade.impl.endpoint.mqtt.MqttTopicUtil;
import com.scene.mesh.foundation.api.message.IMessageProducer;
import com.scene.mesh.foundation.api.message.MessageTopic;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.model.event.Event;
import io.moquette.broker.Server;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class DefaultEventListener implements IEventListener {

    // MQTT broker
    @Autowired
    @Lazy
    // MQTT server
    private Server server;

    // websocket session manager

    private IMessageProducer messageProducer;

    public DefaultEventListener(IMessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @Override
    public void onInboundEvent(Event event) {
        log.info("onInboundEvent: {}", SimpleObjectHelper.objectData2json(event));
        try {
            String topicName = "inbound_events";
            this.messageProducer.send(new MessageTopic(topicName),event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOutboundEvent(Event event) {

    }

    public void onErrorEvent(Event event) {
        String opinion = (String) event.getPayloadVal("opinion");
        String clientId = (String) event.getPayloadVal("clientId");
        String protocol = (String) event.getPayloadVal("protocol");
        String protocolSessionId = (String) event.getPayloadVal("protocolSessionId");

        if (protocol.equals(ProtocolType.MQTT.name())) {
            MqttPublishMessage publishMessage = MqttMessageBuilders.publish()
                    .topicName(MqttTopicUtil.getErrorTopic(clientId))  // 设置消息主题
                    .qos(MqttQoS.AT_LEAST_ONCE)    // 设置QoS级别
                    .retained(false)               // 是否为保留消息
                    .payload(Unpooled.wrappedBuffer(opinion.getBytes(StandardCharsets.UTF_8)))
                    .build();

            server.internalPublish(publishMessage, null);
        } else {
            // TODO 其他协议的 error 处理
        }
    }

}
