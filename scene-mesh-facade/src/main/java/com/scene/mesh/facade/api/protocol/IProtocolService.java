package com.scene.mesh.facade.api.protocol;

import com.scene.mesh.facade.api.outbound.OutboundMessage;
import com.scene.mesh.model.protocol.ProtocolType;

public interface IProtocolService {

    /**
     * 协议类型
     * @return
     */
    ProtocolType getProtocolType();
    /**
     * 发送 outboundMessage
     * @param outboundMessage
     * @return
     */
    boolean send(OutboundMessage outboundMessage);
}
