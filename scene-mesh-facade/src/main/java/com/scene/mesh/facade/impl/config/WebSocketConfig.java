package com.scene.mesh.facade.impl.config;

import com.scene.mesh.facade.impl.protocol.websocket.WebSocketHandler;
import com.scene.mesh.facade.impl.protocol.websocket.WebSocketHandshakeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket config
 */
@Configuration
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

    private final WebSocketHandler webSocketHandler;

    public WebSocketConfig(WebSocketHandshakeInterceptor webSocketHandshakeInterceptor, WebSocketHandler webSocketHandler) {
        this.webSocketHandshakeInterceptor = webSocketHandshakeInterceptor;
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
            String path = "/ws/v1";
            log.info("register WebSocket handler: {} to path {}", webSocketHandshakeInterceptor.getClass().getSimpleName(), path);
            registry.addHandler(webSocketHandler, path)
                    .setAllowedOrigins("*")
                    .addInterceptors(webSocketHandshakeInterceptor);
    }

}