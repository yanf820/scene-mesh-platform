package com.scene.mesh.facade.spec.inboud;

/**
 * handle inbound message, support all protocols
 */
public interface InboundMessageHandler {

    void handle(InboundMessage inboundMessage);
}
