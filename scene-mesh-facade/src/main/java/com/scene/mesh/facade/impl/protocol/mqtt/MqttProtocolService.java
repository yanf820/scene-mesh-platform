package com.scene.mesh.facade.impl.protocol.mqtt;

import com.scene.mesh.facade.spec.outbound.OutboundMessage;
import com.scene.mesh.facade.spec.outbound.OutboundMessageType;
import com.scene.mesh.facade.spec.protocol.IProtocolService;
import com.scene.mesh.model.protocol.ProtocolType;
import io.moquette.broker.Server;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class MqttProtocolService implements IProtocolService {

    private final Server server;

    public MqttProtocolService(@Lazy Server server) {
        this.server = server;
    }

    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.MQTT;
    }

    @Override
    public void send(OutboundMessage outboundMessage) {

        String topicName = null;
        if (OutboundMessageType.ERROR.equals(outboundMessage.getOutboundMessageType())) {
            topicName = MqttTopicUtil.getErrorTopic(outboundMessage.getTerminalId());
        }else {
            topicName = MqttTopicUtil.getActionTopic(outboundMessage.getTerminalId());
        }

        MqttPublishMessage message = MqttMessageBuilders.publish()
                .topicName(topicName)
                .payload(Unpooled.copiedBuffer(outboundMessage.getMessage(), StandardCharsets.UTF_8))
                .qos(MqttQoS.AT_LEAST_ONCE) // 根据需要设置QoS
                .build();
        server.internalPublish(message, outboundMessage.getTerminalId());
    }

}
