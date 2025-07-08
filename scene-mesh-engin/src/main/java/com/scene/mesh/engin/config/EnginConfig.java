package com.scene.mesh.engin.config;

import com.scene.mesh.engin.model.SceneMatchedResult;
import com.scene.mesh.engin.processor.cache.CacheProcessor;
import com.scene.mesh.engin.processor.cache.CacheTrigger;
import com.scene.mesh.foundation.spec.cache.ICache;
import com.scene.mesh.foundation.spec.message.IMessageConsumer;
import com.scene.mesh.foundation.spec.message.IMessageProducer;
import com.scene.mesh.model.event.Event;
import com.scene.mesh.service.impl.product.DefaultProductService;
import com.scene.mesh.service.spec.product.IProductService;
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
import com.scene.mesh.service.spec.cache.MutableCacheService;
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

import java.time.Duration;
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

    // 环境和主题配置
    @Value("${scene-mesh.environment.name}")
    private String environment;

    @Value("${scene-mesh.topics.inbound-events}")
    private String inboundEventsTopic;

    @Value("${scene-mesh.topics.matched-result}")
    private String matchedResultTopic;

    // 消息类配置
    @Value("${scene-mesh.message-classes.event}")
    private String eventMessageClass;

    @Value("${scene-mesh.message-classes.scene-matched-result}")
    private String sceneMatchedResultClass;

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

    // ======== cache scheduler
    @Bean(name = "cron-trigger")
    public CacheTrigger cacheTrigger(){
        return new CacheTrigger(Duration.ofSeconds(5));
    }

    @Bean(name = "cache-processor")
    public CacheProcessor cacheProcessor(MutableCacheService mutableCacheService){
        return new CacheProcessor(mutableCacheService);
    }
}
