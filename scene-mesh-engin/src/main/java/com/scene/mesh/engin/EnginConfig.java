package com.scene.mesh.engin;

import com.scene.mesh.engin.model.SceneMatchedResult;
import com.scene.mesh.foundation.spec.cache.ICache;
import com.scene.mesh.foundation.spec.message.IMessageConsumer;
import com.scene.mesh.foundation.spec.message.IMessageProducer;
import com.scene.mesh.model.event.Event;
import com.scene.mesh.service.spec.scene.ISceneService;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Configuration;
import com.scene.mesh.foundation.impl.component.SpringComponentProvider;
import com.scene.mesh.foundation.impl.processor.execute.DefaultProcessManager;
import com.scene.mesh.foundation.impl.processor.flink.FlinkProcessExecutor;
import com.scene.mesh.foundation.impl.cache.RedisCache;
import com.scene.mesh.foundation.impl.message.RedisMessageConsumer;
import com.scene.mesh.foundation.impl.message.RedisMessageProducer;
import com.scene.mesh.service.spec.cache.MutableCacheService;
import com.scene.mesh.service.impl.scene.SceneService;
import com.scene.mesh.service.impl.ai.model.zhipu.ZhiPuChatModel;
import com.scene.mesh.engin.processor.then.operator.AgentOperator;
import com.scene.mesh.engin.processor.then.operator.NonAgentOperator;
import com.scene.mesh.engin.processor.then.operator.OperatorManager;
import com.scene.mesh.engin.processor.when.EventProducer;
import com.scene.mesh.engin.processor.when.EventSinker;
import com.scene.mesh.engin.processor.then.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

@Configuration
@Slf4j
public class EnginConfig {

    // 添加这个Bean来处理占位符
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        configurer.setProperties(Objects.requireNonNull(yaml.getObject()));

        return configurer;
    }

    // 基础设施配置
    @Value("${scene-mesh.infrastructure.flink-web.host}")
    private String webHost;

    @Value("${scene-mesh.infrastructure.flink-web.port}")
    private String webPort;

    @Value("${scene-mesh.infrastructure.redis.host}")
    private String redisHost;

    @Value("${scene-mesh.infrastructure.redis.port}")
    private String redisPort;

    @Value("${scene-mesh.infrastructure.message.batch-size}")
    private String batchSize;

    @Value("${scene-mesh.infrastructure.message.timeout-seconds}")
    private String timeoutSeconds;

    // 环境和主题配置
    @Value("${scene-mesh.environment.name}")
    private String environment;

    @Value("${scene-mesh.topics.inbound-events}")
    private String inboundEventsTopic;

    @Value("${scene-mesh.topics.matched-result}")
    private String matchedResultTopic;

    // AI配置
    @Value("${scene-mesh.ai.provider.zhipu.api-key}")
    private String zhipuApiKey;

    @Value("${scene-mesh.ai.mcp.server.url}")
    private String mcpServerUrl;

    // 消息类配置
    @Value("${scene-mesh.message-classes.event}")
    private String eventMessageClass;

    @Value("${scene-mesh.message-classes.scene-matched-result}")
    private String sceneMatchedResultClass;

    // ===== 基础组件 =====

    @Bean
    public SpringComponentProvider componentProvider() {
        return new SpringComponentProvider();
    }

    @Bean
    public FlinkProcessExecutor executor(SpringComponentProvider springComponentProvider) {
        FlinkProcessExecutor executor = new FlinkProcessExecutor(springComponentProvider);
        executor.setWebHost(webHost);
        executor.setWebPort(Integer.parseInt(webPort));
        executor.__init__();
        return executor;
    }

    @Bean
    public DefaultProcessManager processManager(FlinkProcessExecutor executor) {
        DefaultProcessManager manager = new DefaultProcessManager();
        manager.setExecutor(executor);
        return manager;
    }

    // ===== 缓存和存储 =====

    @Bean
    public RedisCache iCache() {
        return new RedisCache(redisHost, Integer.parseInt(redisPort));
    }

    @Bean
    public MutableCacheService mutableCache(ICache iCache) {
        return new MutableCacheService(iCache);
    }

    @Bean
    public SceneService sceneService() {
        return new SceneService();
    }

    // ===== 消息组件 =====

    @Bean
    public RedisMessageConsumer messageConsumer() {
        RedisMessageConsumer consumer = new RedisMessageConsumer();
        consumer.setBatchSize(Integer.parseInt(batchSize));
        consumer.setHost(redisHost);
        consumer.setPort(Integer.parseInt(redisPort));
        consumer.setTimeoutSeconds(Integer.parseInt(timeoutSeconds));
        consumer.__init__();
        return consumer;
    }

    @Bean
    public RedisMessageProducer messageProducer() {
        RedisMessageProducer producer = new RedisMessageProducer();
        producer.setHost(redisHost);
        producer.setPort(Integer.parseInt(redisPort));
        producer.__init__();
        return producer;
    }

    // ===== AI 和操作组件 =====

    @Bean
    public ZhiPuChatModel zhiPuChatModel() {
        return new ZhiPuChatModel(zhipuApiKey);
    }

    @Bean
    public McpSyncClient sseMcpClient() {
        try {
            // 创建 SSE 传输层
            WebClient.Builder webClientBuilder = WebClient.builder()
                    .baseUrl(mcpServerUrl)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE);

            McpClientTransport transport = WebFluxSseClientTransport
                    .builder(webClientBuilder)
                    .sseEndpoint("/sse")
                    .build();

            McpSyncClient mcpSyncClient = McpClient.sync(transport).build();
            return mcpSyncClient;

        } catch (Exception e) {
            log.error("Failed to create Scene Mesh MCP client", e);
            throw new RuntimeException("MCP client initialization failed", e);
        }
    }

    @Bean
    public ToolCallbackProvider sceneMeshToolCallbackProvider(McpSyncClient mcpSyncClient) {
        mcpSyncClient.initialize();

        // 🔍 打印服务器信息
        log.info("🔍 连接的MCP服务器信息: {}", mcpSyncClient.getServerInfo());
        log.info("🔍 服务器能力: {}", mcpSyncClient.getServerCapabilities());

        SyncMcpToolCallbackProvider provider = new SyncMcpToolCallbackProvider(mcpSyncClient);

        // 🔍 打印工具详情
        ToolCallback[] callbacks = provider.getToolCallbacks();
        log.info("🔍 从MCP服务器获取到 {} 个工具:", callbacks.length);
        for (int i = 0; i < callbacks.length; i++) {
            log.info("  {}. {} - {}", i + 1,
                    callbacks[i].getToolDefinition().name(),
                    callbacks[i].getToolDefinition().description());
        }

        return provider;
    }

    @Bean
    public AgentOperator agentOperator(ZhiPuChatModel zhiPuChatModel, ToolCallbackProvider toolCallbackProvider) {
        return new AgentOperator(List.of(zhiPuChatModel),toolCallbackProvider);
    }

    @Bean
    public NonAgentOperator nonAgentOperator() {
        return new NonAgentOperator();
    }

    @Bean
    public OperatorManager operatorManager(AgentOperator agentOperator, NonAgentOperator nonAgentOperator) {
        return new OperatorManager(List.of(agentOperator, nonAgentOperator));
    }

    // ===== When Graph 组件 =====

    @Bean(name = "scene-event-producer")
    public EventProducer sceneEventProducer(IMessageConsumer messageConsumer) throws ClassNotFoundException {
        EventProducer producer = new EventProducer();
        producer.setEnv(environment);
        producer.setMessageClass((Class<Event>) Class.forName(eventMessageClass));
        producer.setMessageConsumer(messageConsumer);
        producer.setTopicName(inboundEventsTopic);
        return producer;
    }

    @Bean(name = "scene-match-sinker")
    public EventSinker sceneMatchSinker(IMessageProducer messageProducer) {
        EventSinker sinker = new EventSinker();
        sinker.setTopicName(matchedResultTopic);
        sinker.setMessageProducer(messageProducer);
        return sinker;
    }

    // ===== Then Graph 组件 =====

    @Bean(name = "matched-scene-producer")
    public MatchedSceneProducer matchedSceneProducer(IMessageConsumer messageConsumer) throws ClassNotFoundException {
        MatchedSceneProducer producer = new MatchedSceneProducer();
        producer.setEnv(environment);
        producer.setMessageClass((Class<SceneMatchedResult>) Class.forName(sceneMatchedResultClass));
        producer.setMessageConsumer(messageConsumer);
        producer.setTopicName(matchedResultTopic);
        return producer;
    }

    @Bean(name = "scene-selector")
    public SceneSelector sceneSelector(MutableCacheService mutableCacheService, ISceneService sceneService) throws ClassNotFoundException {
        SceneSelector selector = new SceneSelector();
        selector.setEnv(environment);
        selector.setCacheService(mutableCacheService);
        selector.setSceneService(sceneService);
        return selector;
    }

    @Bean(name = "operation-handler")
    public OperationHandler operationHandler(MutableCacheService mutableCacheService,OperatorManager operatorManager) {
        return new OperationHandler(mutableCacheService, operatorManager);
    }
}
