package com.scene.mesh.facade.impl.protocol.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TerminalSessionManager {

    private final Map<String, WebSocketSession> terminalSessionMap;

    public TerminalSessionManager() {
        this.terminalSessionMap = new ConcurrentHashMap<>();
    }

    public void registerSession(String terminalId, WebSocketSession session) {
        this.terminalSessionMap.put(terminalId, session);
    }

    public void unregisterSession(String terminalId) {
        this.terminalSessionMap.remove(terminalId);
    }

    public WebSocketSession getSession(String terminalId) {
        WebSocketSession session = this.terminalSessionMap.get(terminalId);
        if (session == null) {
            log.error("session not found with terminalId: {}.", terminalId);
        }
        return session;
    }
}
