package com.scene.mesh.facade.impl.protocol.websocket;

import com.scene.mesh.facade.spec.common.ITerminalAuthenticator;
import com.scene.mesh.model.protocol.ProtocolType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;

@Slf4j
@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final ITerminalAuthenticator terminalAuthenticator;

    public WebSocketHandshakeInterceptor(ITerminalAuthenticator terminalAuthenticator) {
        this.terminalAuthenticator = terminalAuthenticator;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        String productId = request.getHeaders().getFirst("productId");
        String terminalId = request.getHeaders().getFirst("terminalId");
        String passwordString = request.getHeaders().getFirst("secretKey");

        return terminalAuthenticator.authenticate(productId, terminalId, passwordString, ProtocolType.WEBSOCKET);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {

        String productId = request.getHeaders().getFirst("productId");
        String terminalId = request.getHeaders().getFirst("terminalId");

        if (exception != null) {
            log.error("terminal authenticate failed - protocol: {}, productId: {}, terminalId: {}, exception",
                    ProtocolType.WEBSOCKET,productId,terminalId, exception);
        } else {
            log.info("terminal authenticate success - protocol: {}, productId: {}, terminalId: {}",
                    ProtocolType.WEBSOCKET,productId,terminalId);
        }
    }
}