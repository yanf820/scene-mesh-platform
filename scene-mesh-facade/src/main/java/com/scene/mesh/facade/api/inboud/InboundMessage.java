package com.scene.mesh.facade.api.inboud;

import com.scene.mesh.model.protocol.ProtocolType;
import lombok.Data;

@Data
public class InboundMessage {
    private String message;
    private String terminalId;

    public InboundMessage(String terminalId, String message) {
        this.message = message;
        this.terminalId = terminalId;
    }
}
