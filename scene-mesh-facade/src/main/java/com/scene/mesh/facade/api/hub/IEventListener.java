package com.scene.mesh.facade.api.hub;

import com.scene.mesh.model.event.Event;

/**
 * 所有协议共享的消息处理
 */
public interface IEventListener {

    void onInboundEvent(Event event);

    void onOutboundEvent(Event event);

    void onErrorEvent(Event event);

}
