package com.scene.mesh.facade.api.inboud;

/**
 * 入站消息处理器
 */
public interface InboundMessageHandler {

    void handle(InboundMessage inboundMessage);
}
