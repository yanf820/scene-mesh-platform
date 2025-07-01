package com.scene.mesh.facade.spec.inboud;

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
