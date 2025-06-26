package com.scene.mesh.facade.impl.common;

import com.scene.mesh.facade.api.common.IMessageExchanger;
import com.scene.mesh.facade.api.outbound.IOutboundMessageHandler;
import com.scene.mesh.facade.api.outbound.OutboundMessage;
import com.scene.mesh.facade.api.outbound.OutboundMessageType;
import com.scene.mesh.foundation.api.message.IMessageProducer;
import com.scene.mesh.foundation.api.message.MessageTopic;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.model.action.Action;
import com.scene.mesh.model.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DefaultMessageExchanger implements IMessageExchanger {

    @Autowired
    private IOutboundMessageHandler outboundMessageHandler;

    @Autowired
    private IMessageProducer messageProducer;

    @Override
    public void handleInboundEvent(Event event) {
        log.info("onInboundEvent: {}", SimpleObjectHelper.objectData2json(event));
        try {
            String topicName = "inbound_events";
            this.messageProducer.send(new MessageTopic(topicName), event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleOutboundAction(Action action) {
        OutboundMessage outboundMessage = new OutboundMessage();
        outboundMessage.setTerminalId(action.getTerminalId());
        outboundMessage.setMessage(SimpleObjectHelper.objectData2json(action));
        outboundMessage.setOutboundMessageType(OutboundMessageType.ACTION);
        this.outboundMessageHandler.handle(outboundMessage);
    }

    @Override
    public void handleErrorOutboundMessage(OutboundMessage outboundMessage) {
        outboundMessage.setOutboundMessageType(OutboundMessageType.ERROR);
        this.outboundMessageHandler.handle(outboundMessage);
    }
}
