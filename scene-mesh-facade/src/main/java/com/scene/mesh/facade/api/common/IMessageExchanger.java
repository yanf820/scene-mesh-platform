package com.scene.mesh.facade.api.common;

import com.scene.mesh.facade.api.outbound.OutboundMessage;
import com.scene.mesh.model.action.Action;
import com.scene.mesh.model.event.Event;

/**
 * 协议共享的消息交换器
 */
public interface IMessageExchanger {

    void handleInboundEvent(Event event);

    void handleOutboundAction(Action action);

    void handleErrorOutboundMessage(OutboundMessage outboundMessage);

}
