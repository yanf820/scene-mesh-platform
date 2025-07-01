package com.scene.mesh.facade.spec.protocol;

import com.scene.mesh.facade.spec.outbound.OutboundMessage;
import com.scene.mesh.model.protocol.ProtocolType;

public interface IProtocolService {

    ProtocolType getProtocolType();

    void send(OutboundMessage outboundMessage);
}
