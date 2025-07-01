package com.scene.mesh.facade.spec.outbound;

import lombok.Data;

@Data
public class OutboundMessage {

    private OutboundMessageType outboundMessageType;

    private String terminalId;

    private String message;

}
