package com.scene.mesh.engin.config;

import com.scene.mesh.foundation.spec.api.ApiClient;
import com.scene.mesh.foundation.spec.cache.ICache;
import com.scene.mesh.service.impl.ai.model.zhipu.ZhiPuChatModel;
import com.scene.mesh.service.impl.event.DefaultMetaEventService;
import com.scene.mesh.service.impl.product.DefaultProductService;
import com.scene.mesh.service.impl.scene.DefaultSceneService;
import com.scene.mesh.service.impl.speech.DefaultSpeechService;
import com.scene.mesh.service.spec.cache.MutableCacheService;
import com.scene.mesh.service.spec.event.IMetaEventService;
import com.scene.mesh.service.spec.product.IProductService;
import com.scene.mesh.service.spec.scene.ISceneService;
import com.scene.mesh.service.spec.speech.ISpeechService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    // AI配置
    @Value("${scene-mesh.ai.provider.zhipu.api-key}")
    private String zhipuApiKey;

    @Bean
    public MutableCacheService mutableCache(ICache iCache, ApiClient apiClient) {
        return new MutableCacheService(iCache,apiClient);
    }

    @Bean
    public IMetaEventService metaEventService() {
        return new DefaultMetaEventService();
    }

    @Bean
    public ISpeechService speechService() {
        return new DefaultSpeechService();
    }

    @Bean
    public IProductService productService(MutableCacheService mutableCacheService) {
        return new DefaultProductService(mutableCacheService);
    }

    @Bean
    public ISceneService sceneService() {
        return new DefaultSceneService();
    }

    @Bean
    public ZhiPuChatModel zhiPuChatModel() {
        return new ZhiPuChatModel(zhipuApiKey);
    }


}
