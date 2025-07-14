package com.scene.mesh.facade.impl.outbound;

import com.scene.mesh.facade.spec.outbound.IOutboundMessageHandler;
import com.scene.mesh.facade.spec.outbound.OutboundMessage;
import com.scene.mesh.facade.spec.protocol.IProtocolService;
import com.scene.mesh.facade.spec.protocol.IProtocolServiceManager;
import com.scene.mesh.facade.spec.protocol.TerminalProtocolStateManager;
import com.scene.mesh.model.protocol.ProtocolType;
import org.springframework.stereotype.Component;

@Component
public class DefaultOutboundMessageHandler implements IOutboundMessageHandler {

    private final TerminalProtocolStateManager terminalProtocolStateManager;

    private final IProtocolServiceManager protocolServiceManager;

    public DefaultOutboundMessageHandler(TerminalProtocolStateManager terminalProtocolStateManager, IProtocolServiceManager protocolServiceManager) {
        this.terminalProtocolStateManager = terminalProtocolStateManager;
        this.protocolServiceManager = protocolServiceManager;
    }

    @Override
    public void handle(OutboundMessage outboundMessage) {
        String terminalId = outboundMessage.getTerminalId();
        ProtocolType currentProtocolType = this.terminalProtocolStateManager.getProtocolState(terminalId);
        IProtocolService protocolService = this.protocolServiceManager.getProtocolService(currentProtocolType);
        if (protocolService == null) {
            throw new RuntimeException("can't find protocol service for protocol type: websocket");
        }
        protocolService.send(outboundMessage);
    }

}
