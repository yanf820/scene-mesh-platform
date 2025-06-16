package com.scene.mesh.facade.impl.config;

import com.scene.mesh.foundation.api.cache.ICache;
import com.scene.mesh.foundation.api.message.IMessageProducer;
import com.scene.mesh.foundation.impl.cache.RedisCache;
import com.scene.mesh.foundation.impl.message.JsonMessageSerializer;
import com.scene.mesh.foundation.impl.message.RedisMessageProducer;
import com.scene.mesh.model.event.MockMetaEventRepository;
import com.scene.mesh.model.scene.MockSceneRepository;
import com.scene.mesh.service.api.cache.MutableCacheService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FoundationConfig {

    @Value("${redis.connection.host}")
    private String redisHost;

    @Value("${redis.connection.port}")
    private int redisPort;

    @Bean
    public MutableCacheService mutableCacheService(ICache<String,Object> redisCache,
                                                   MockSceneRepository mockSceneRepository,
                                                   MockMetaEventRepository mockMetaEventRepository){
        return new MutableCacheService(redisCache,mockSceneRepository,mockMetaEventRepository);
    }

    @Bean
    public ICache<String,Object> redisCache(){
        return new RedisCache<>(redisHost,redisPort);
    }

    @Bean
    public MockSceneRepository mockSceneRepository(){
        return new MockSceneRepository();
    }

    @Bean
    public MockMetaEventRepository mockMetaEventRepository(){
        return new MockMetaEventRepository();
    }

    @Bean
    public IMessageProducer messageProducer(){
        RedisMessageProducer messageProducer = new RedisMessageProducer();
        messageProducer.setHost(redisHost);
        messageProducer.setPort(redisPort);
        messageProducer.setSerializer(new JsonMessageSerializer());
        messageProducer.__init__();
        return messageProducer;
    }


}
