package com.scene.mesh.facade.impl.protocol.websocket;

import com.scene.mesh.facade.spec.outbound.OutboundMessage;
import com.scene.mesh.facade.spec.protocol.IProtocolService;
import com.scene.mesh.facade.spec.protocol.IProtocolServiceManager;
import com.scene.mesh.model.protocol.ProtocolType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@Service
public class WebSocketProtocolService implements IProtocolService {

    private final TerminalSessionManager terminalSessionManager;

    public WebSocketProtocolService(TerminalSessionManager terminalSessionManager) {
        this.terminalSessionManager = terminalSessionManager;
    }

    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.WEBSOCKET;
    }

    @Override
    public void send(OutboundMessage outboundMessage) {
        String terminalId = outboundMessage.getTerminalId();
        WebSocketSession session = this.terminalSessionManager.getSession(terminalId);
        if (session == null) {
            log.error("Can not find terminal session with terminalId:{}", terminalId);
            return;
        }
        try {
            session.sendMessage(new TextMessage(outboundMessage.getMessage()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
