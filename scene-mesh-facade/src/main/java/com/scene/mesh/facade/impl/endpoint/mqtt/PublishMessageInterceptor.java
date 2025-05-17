package com.scene.mesh.facade.impl.endpoint.mqtt;

import com.scene.mesh.facade.api.ProtocolType;
import com.scene.mesh.facade.api.inboud.InboundMessageHandler;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.foundation.impl.helper.StringHelper;
import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.*;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PublishMessageInterceptor implements InterceptHandler {

    @Autowired
    private InboundMessageHandler inboundMessageHandler;

    @Override
    public String getID() {
        return "PublishMessageInterceptor";
    }

    @Override
    public Class<?>[] getInterceptedMessageTypes() {
        return ALL_MESSAGE_TYPES;
    }

    @Override
    public void onConnect(InterceptConnectMessage msg) {
        log.info("终端已连接: client id - {}", SimpleObjectHelper.objectData2json(msg));
    }

    @Override
    public void onDisconnect(InterceptDisconnectMessage msg) {
        log.info("终端已断开: client id - {}", SimpleObjectHelper.objectData2json(msg));
    }

    @Override
    public void onConnectionLost(InterceptConnectionLostMessage msg) {
        log.info("终端已丢失: client id - {}", SimpleObjectHelper.objectData2json(msg));
    }

    @Override
    public void onPublish(InterceptPublishMessage msg) {
        //转化 payload 为 string
        ByteBuf payload = msg.getPayload();
        String messageContent = StringHelper.nettyByteBuf2String(payload);
        String topicName = msg.getTopicName();
        String clientId = msg.getClientID();
        String metaEventId = StringUtils.substringAfterLast(topicName, "/");
        log.debug("接收到消息: client id - {}, topic - {}, payload - {}",
                clientId, topicName, messageContent);

        //交给 inboundMessageHandler 处理
        InboundMessageHandler.InboundMessage inboundMessage = new InboundMessageHandler.InboundMessage(
                metaEventId, clientId,messageContent,ProtocolType.MQTT,null);
        this.inboundMessageHandler.handle(inboundMessage);
    }

    @Override
    public void onSubscribe(InterceptSubscribeMessage msg) {
        log.info("subscribe msg - {}", SimpleObjectHelper.objectData2json(msg));
    }

    @Override
    public void onUnsubscribe(InterceptUnsubscribeMessage msg) {
        log.info("unsubscribe msg - {}", SimpleObjectHelper.objectData2json(msg));
    }

    @Override
    public void onMessageAcknowledged(InterceptAcknowledgedMessage msg) {
        log.info("acknowledge msg - {}", SimpleObjectHelper.objectData2json(msg));
    }

    @Override
    public void onSessionLoopError(Throwable error) {
        log.info("session loop error - {}", SimpleObjectHelper.objectData2json(error));
    }
}
