package com.scene.mesh.facade.impl.protocol.websocket;

import com.scene.mesh.facade.spec.outbound.OutboundMessage;
import com.scene.mesh.facade.spec.protocol.IProtocolService;
import com.scene.mesh.model.protocol.ProtocolType;

public class WebSocketProtocolService implements IProtocolService {
    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.WEBSOCKET;
    }

    @Override
    public void send(OutboundMessage outboundMessage) {
    }
}
