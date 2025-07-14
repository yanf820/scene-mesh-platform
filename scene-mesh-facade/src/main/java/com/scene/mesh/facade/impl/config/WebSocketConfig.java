package com.scene.mesh.facade.impl.config;

import com.scene.mesh.facade.impl.protocol.websocket.WebSocketHandler;
import com.scene.mesh.facade.impl.protocol.websocket.WebSocketHandshakeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * WebSocket config
 */
@Configuration
@EnableWebSocket
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

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(2 * 1024 * 1024); // Set max text message size to 1MB
        container.setMaxBinaryMessageBufferSize(2 * 1024 * 1024); // Set max binary message size to 1MB
        return container;
    }

}