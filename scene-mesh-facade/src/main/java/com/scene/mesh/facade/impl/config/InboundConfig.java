package com.scene.mesh.facade.impl.config;

import com.scene.mesh.facade.api.inboud.InboundMessageInterceptor;
import com.scene.mesh.facade.impl.inbound.MessageLegalityChecker;
import com.scene.mesh.facade.impl.inbound.MessageToEventConvertor;
import com.scene.mesh.service.api.event.IMetaEventService;
import com.scene.mesh.service.impl.event.DefaultMetaEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class InboundConfig {

    @Bean
    public IMetaEventService  metaEventService() {
        return new DefaultMetaEventService();
    }

    @Bean
    public List<InboundMessageInterceptor> messageInterceptors(IMetaEventService metaEventService) {
        List<InboundMessageInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new MessageLegalityChecker(metaEventService));
        interceptors.add(new MessageToEventConvertor());
        return interceptors;
    }
}
