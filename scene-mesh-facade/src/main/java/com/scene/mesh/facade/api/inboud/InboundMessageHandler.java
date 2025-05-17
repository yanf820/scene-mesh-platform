package com.scene.mesh.facade.api.inboud;

import com.scene.mesh.facade.api.ProtocolType;
import lombok.Data;

/**
 * 入站消息处理器
 */
public interface InboundMessageHandler {

    void handle(InboundMessage inboundMessage);

    @Data
    class InboundMessage {
        private String metaEventId;
        private ProtocolType protocolType;
        private String protocolSessionId;
        private String message;
        private String clientId;

        public InboundMessage(String metaEventId,String clientId, String message, ProtocolType protocolType, String protocolSessionId) {
            this.protocolType = protocolType;
            this.protocolSessionId = protocolSessionId;
            this.message = message;
            this.clientId = clientId;
            this.metaEventId = metaEventId;
        }
    }
}
