package com.scene.mesh.facade.spec.common;

import com.scene.mesh.facade.spec.outbound.OutboundMessage;
import com.scene.mesh.model.action.Action;
import com.scene.mesh.model.event.Event;

/**
 * Protocol-shared message exchanger
 */
public interface IMessageExchanger {

    void handleInboundEvent(Event event);

    void handleOutboundAction(Action action);

    void handleErrorOutboundMessage(OutboundMessage outboundMessage);

}
