package com.scene.mesh.facade.impl.config;

import com.scene.mesh.facade.impl.protocol.mqtt.PublishMessageInterceptor;
import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import io.moquette.broker.security.IAuthenticator;
import io.moquette.interception.InterceptHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Configuration
@Slf4j
public class MqttBrokerConfig {

    @Value("${mqtt.server.port}")
    private int port;

    @Value("${mqtt.server.host}")
    private String host;

    @Value("${mqtt.server.allowAnonymous}")
    private boolean allowAnonymous;

    @Value("${mqtt.server.messageSize}")
    private int messageSize;

    @Bean
    public Server mqttBroker(List<InterceptHandler> interceptHandlers, IAuthenticator authenticator) throws IOException {
        log.info("启动 MQTT broker - TCP: {}:{}", host, port);

        Properties properties = new Properties();
        properties.setProperty(IConfig.HOST_PROPERTY_NAME, host);
        properties.setProperty(IConfig.PORT_PROPERTY_NAME, String.valueOf(port));
        // 允许匿名访问
        properties.setProperty(IConfig.ALLOW_ANONYMOUS_PROPERTY_NAME, String.valueOf(allowAnonymous));
        // 其他配置
        properties.setProperty(IConfig.NETTY_MAX_BYTES_PROPERTY_NAME, String.valueOf(messageSize));
        // WebSocket 配置
//        properties.setProperty(IConfig.WEB_SOCKET_PATH_PROPERTY_NAME, "/sm");
//        properties.setProperty(IConfig.WEB_SOCKET_PORT_PROPERTY_NAME, "1885"); // 使用不同端口
        IConfig config = new MemoryConfig(properties);

        Server server = new Server();

        server.startServer(config, interceptHandlers, null,authenticator,null);

        return server;
    }

    @Bean
    public List<InterceptHandler> interceptHandlers(PublishMessageInterceptor publishMessageInterceptor) {
        return List.of(publishMessageInterceptor);
    }
}
