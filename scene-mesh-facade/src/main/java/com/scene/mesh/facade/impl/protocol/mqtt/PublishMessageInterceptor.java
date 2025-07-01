package com.scene.mesh.facade.impl.protocol.mqtt;

import com.scene.mesh.facade.spec.protocol.TerminalProtocolStateManager;
import com.scene.mesh.model.protocol.ProtocolType;
import com.scene.mesh.facade.spec.inboud.InboundMessage;
import com.scene.mesh.facade.spec.inboud.InboundMessageHandler;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.foundation.impl.helper.StringHelper;
import com.scene.mesh.model.terminal.TerminalStatus;
import com.scene.mesh.service.spec.product.IProductService;
import com.scene.mesh.service.spec.terminal.ITerminalService;
import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.*;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PublishMessageInterceptor implements InterceptHandler {

    private final InboundMessageHandler inboundMessageHandler;

    private final TerminalProtocolStateManager terminalProtocolStateManager;

    private final IProductService productService;

    private final ITerminalService terminalService;

    public PublishMessageInterceptor(InboundMessageHandler inboundMessageHandler, TerminalProtocolStateManager terminalProtocolStateManager, IProductService productService, ITerminalService terminalService) {
        this.inboundMessageHandler = inboundMessageHandler;
        this.terminalProtocolStateManager = terminalProtocolStateManager;
        this.productService = productService;
        this.terminalService = terminalService;
    }

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
        this.terminalService.updateStatus(msg.getUsername(), msg.getClientID(),  TerminalStatus.ONLINE);
        this.terminalProtocolStateManager.setProtocolState(msg.getClientID(), ProtocolType.MQTT);
        log.info("The terminal is connected : terminal id - {}", msg.getClientID());
    }

    @Override
    public void onDisconnect(InterceptDisconnectMessage msg) {
        this.terminalService.updateStatus(msg.getUsername(),msg.getClientID(), TerminalStatus.OFFLINE); // TODO 补充
        this.terminalProtocolStateManager.removeProtocolState(msg.getClientID());
        log.info("The terminal has disconnected : terminal id - {}", msg.getClientID());
    }

    @Override
    public void onConnectionLost(InterceptConnectionLostMessage msg) {
        this.terminalService.updateStatus(msg.getUsername(), msg.getClientID(), TerminalStatus.OFFLINE); // TODO 补充
        this.terminalProtocolStateManager.removeProtocolState(msg.getClientID());
        log.info("The terminal has been lost : terminal id - {}", msg.getClientID());
    }

    @Override
    public void onPublish(InterceptPublishMessage msg) {
        //转化 payload 为 string
        ByteBuf payload = msg.getPayload();
        String messageContent = StringHelper.nettyByteBuf2String(payload);
        String topicName = msg.getTopicName();
        String clientId = msg.getClientID();

        log.debug("Received terminal message: terminal id - {}, topic - {}, payload - {}",
                clientId, topicName, messageContent);

        //交给 inboundMessageHandler 处理
        InboundMessage inboundMessage = new InboundMessage(
                clientId,messageContent);
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
