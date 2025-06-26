package com.scene.mesh.facade.impl.protocol.websocket;

import com.scene.mesh.facade.api.outbound.OutboundMessage;
import com.scene.mesh.facade.api.protocol.IProtocolService;
import com.scene.mesh.model.protocol.ProtocolType;

public class WebSocketProtocolService implements IProtocolService {
    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.WEBSOCKET;
    }

    @Override
    public boolean send(OutboundMessage outboundMessage) {
        return false;
    }
}
