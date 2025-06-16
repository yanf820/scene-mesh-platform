## 配置

### scene-mesh-facade

见 scene-mesh-facade 工程下 resources/application.yml

```
# MQTT Server 配置（嵌入式启动,无需远端独立服务）
mqtt:
  server:
    host: 127.0.0.1
    port: 1883
    allowAnonymous: true
    messageSize: 262144 # 256KB

# redis 配置，用于接收事件消息
redis:
  connection:
    host: 127.0.0.1
    port: 6379
```

### scene-mesh-engin

见 scene-mesh-engin 工程下 resources/engin.xml 

#### flink本地配置

```
<!-- flink 执行器 -->
<bean id="executor" class="com.scene.mesh.foundation.impl.processor.flink.FlinkProcessExecutor" init-method="__init__">
    <constructor-arg ref="componentProvider"/>
    <property name="webHost" value="127.0.0.1"/>
    <property name="webPort" value="8081"/>
</bean>
```

#### redis缓存配置

```
<!-- redis 缓存 -->
<bean id="iCache" class="com.scene.mesh.foundation.impl.cache.RedisCache">
    <constructor-arg value="127.0.0.1"/>
    <constructor-arg value="6379"/>
</bean>
```

#### redis stream 配置

```
<!-- 消息消费者 -->
<bean id="messageConsumer" class="com.scene.mesh.foundation.impl.message.RedisMessageConsumer"
      init-method="__init__">
    <property name="batchSize" value="10"/>
    <property name="host" value="localhost"/>
    <property name="port" value="6379"/>
    <property name="timeoutSeconds" value="1"/>
</bean>

<!-- 消息生产者 -->
<bean id="messageProducer" class="com.scene.mesh.foundation.impl.message.RedisMessageProducer"
      init-method="__init__">
    <property name="host" value="localhost"/>
    <property name="port" value="6379"/>
</bean>
```

#### 动态规则库地址配置

见 scene-mesh-engin 工程下 com/scene/mesh/engin/SceneMeshEnginApplication.java 中

```
private static ProcessorGraph whenGraph() {
    return ProcessorGraphBuilder.createWithId("when")
            .addNode(ProcessorNodeBuilder.createWithId("scene-event-source")
                    .withComponentId("scene-event-producer")
                    .withParallelism(1)
                    .withOutputType(Event.class))
            .enableCepMode(CepModeDescriptor.builder()
                    .enabled(true)
                    .databaseUrl("jdbc:postgresql://127.0.0.1:5432/postgres")
                    .driverName("org.postgresql.Driver")
                    .ruleSource("cep_rules")
                    .username("postgres")
                    .password("scene_mesh")
                    .period(Duration.ofSeconds(10))
                    .keyed(new String[]{"productId", "terminalId"})
                    .parallelism(1)
                    .cepMatchedResultType(new GenericTypeInfo<>(SceneMatchedResult.class)))
            .addNode(ProcessorNodeBuilder.createWithId("scene-match-sink")
                    .withComponentId("scene-match-sinker")
                    .withParallelism(1)
                    .withOutputType(Event.class)
                    .from("scene-event-source")
            )
            .build();
}
```

