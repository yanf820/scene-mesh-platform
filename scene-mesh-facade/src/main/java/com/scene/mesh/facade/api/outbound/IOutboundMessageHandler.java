package com.scene.mesh.facade.api.outbound;

/**
 * 出站消息处理器
 */
public interface IOutboundMessageHandler {

    void handle(OutboundMessage outboundMessage);

}
