package com.scene.mesh.facade.impl.config;

import com.scene.mesh.foundation.impl.cache.RedisCache;
import com.scene.mesh.foundation.spec.api.ApiClient;
import com.scene.mesh.foundation.spec.cache.ICache;
import com.scene.mesh.model.terminal.TerminalRepository;
import com.scene.mesh.service.impl.event.DefaultMetaEventService;
import com.scene.mesh.service.impl.scene.DefaultSceneService;
import com.scene.mesh.service.spec.cache.MutableCacheService;
import com.scene.mesh.service.spec.event.IMetaEventService;
import com.scene.mesh.service.spec.product.IProductService;
import com.scene.mesh.service.spec.scene.ISceneService;
import com.scene.mesh.service.spec.terminal.ITerminalService;
import com.scene.mesh.service.impl.product.DefaultProductService;
import com.scene.mesh.service.impl.terminal.DefaultTerminalService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public IProductService productService(MutableCacheService mutableCacheService) {
        return new DefaultProductService(mutableCacheService);
    }

    @Bean
    public ITerminalService terminalService(TerminalRepository terminalRepository){
        return new DefaultTerminalService(terminalRepository);
    }

    @Bean
    public IMetaEventService metaEventService(MutableCacheService mutableCacheService) {
        return new DefaultMetaEventService(mutableCacheService);
    }

    @Bean
    public ISceneService sceneService(MutableCacheService mutableCacheService) {
        return new DefaultSceneService(mutableCacheService);
    }

    @Bean
    public MutableCacheService mutableCacheService(ICache cache) {
        return new MutableCacheService(cache,null);
    }
}
