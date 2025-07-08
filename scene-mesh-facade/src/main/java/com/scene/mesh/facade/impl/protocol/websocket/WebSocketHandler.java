package com.scene.mesh.facade.impl.protocol.websocket;


import com.scene.mesh.facade.spec.inboud.InboundMessage;
import com.scene.mesh.facade.spec.inboud.InboundMessageHandler;
import com.scene.mesh.facade.spec.protocol.TerminalProtocolStateManager;
import com.scene.mesh.model.protocol.ProtocolType;
import com.scene.mesh.model.terminal.TerminalStatus;
import com.scene.mesh.service.spec.terminal.ITerminalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Component
@Slf4j
public class WebSocketHandler extends AbstractWebSocketHandler {

    private final InboundMessageHandler inboundMessageHandler;

    private final TerminalProtocolStateManager terminalProtocolStateManager;

    private final ITerminalService terminalService;

    private final TerminalSessionManager terminalSessionManager;

    public WebSocketHandler(InboundMessageHandler inboundMessageHandler, TerminalProtocolStateManager terminalProtocolStateManager, ITerminalService terminalService, TerminalSessionManager terminalSessionManager) {
        this.inboundMessageHandler = inboundMessageHandler;
        this.terminalProtocolStateManager = terminalProtocolStateManager;
        this.terminalService = terminalService;
        this.terminalSessionManager = terminalSessionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        String productId = (String) session.getAttributes().get("productId");
        String terminalId = (String) session.getAttributes().get("terminalId");

        // terminal online
        this.terminalService.updateStatus(productId, terminalId, TerminalStatus.ONLINE);
        // set terminal protocol state
        this.terminalProtocolStateManager.setProtocolState(terminalId, ProtocolType.WEBSOCKET);
        // register terminal session
        this.terminalSessionManager.registerSession(terminalId, session);
        log.info("The terminal is connected - protocol:{}, terminal id: {}", ProtocolType.WEBSOCKET, terminalId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String text = message.getPayload();
        String sessionId = session.getId();
        String productId = (String) session.getAttributes().get("productId");
        String terminalId = (String) session.getAttributes().get("terminalId");
        log.debug("Received terminal text message - protocol:{}, product id - {}, terminal id - {}, payload - {}",
                ProtocolType.WEBSOCKET, productId, terminalId, text);
        //交给 inboundMessageHandler 处理
        InboundMessage inboundMessage = new InboundMessage(
                terminalId, text);
        this.inboundMessageHandler.handle(inboundMessage);
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String productId = (String) session.getAttributes().get("productId");
        String terminalId = (String) session.getAttributes().get("terminalId");

        // terminal online
        this.terminalService.updateStatus(productId, terminalId, TerminalStatus.OFFLINE);
        // remove terminal protocol state
        this.terminalProtocolStateManager.removeProtocolState(terminalId);
        // unregister terminal session
        this.terminalSessionManager.unregisterSession(terminalId);
        log.info("The terminal is disconnected - protocol:{}, terminal id: {}", ProtocolType.WEBSOCKET, terminalId);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}