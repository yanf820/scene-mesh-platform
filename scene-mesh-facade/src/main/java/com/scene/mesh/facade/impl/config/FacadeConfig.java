package com.scene.mesh.facade.impl.config;

import com.scene.mesh.facade.impl.common.DefaultTerminalAuthenticator;
import com.scene.mesh.facade.spec.common.ITerminalAuthenticator;
import com.scene.mesh.facade.spec.inboud.InboundMessageInterceptor;
import com.scene.mesh.facade.impl.inbound.MessageLegalityChecker;
import com.scene.mesh.facade.impl.inbound.MessageToEventConvertor;
import com.scene.mesh.facade.spec.protocol.TerminalProtocolStateManager;
import com.scene.mesh.foundation.spec.message.MessageTopic;
import com.scene.mesh.service.spec.cache.MutableCacheService;
import com.scene.mesh.service.spec.event.IMetaEventService;
import com.scene.mesh.service.impl.event.DefaultMetaEventService;
import com.scene.mesh.service.spec.product.IProductService;
import com.scene.mesh.service.spec.terminal.ITerminalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class FacadeConfig {

    @Value("${topic.inbound-event}")
    private String inboundEventTopic;

    @Value("${topic.outbound-action}")
    private String outboundActionTopic;

    @Bean
    public List<InboundMessageInterceptor> messageInterceptors(MutableCacheService mutableCacheService) {
        List<InboundMessageInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new MessageLegalityChecker(mutableCacheService));
        interceptors.add(new MessageToEventConvertor());
        return interceptors;
    }

    @Bean
    public MessageTopic inboundEventTopic(){
        return new MessageTopic(inboundEventTopic);
    }

    @Bean
    public MessageTopic outboundActionTopic(){
        return new MessageTopic(outboundActionTopic);
    }

    @Bean
    public TerminalProtocolStateManager terminalProtocolStateManager(){
        return new TerminalProtocolStateManager();
    }

    @Bean
    public ITerminalAuthenticator terminalAuthenticator(ITerminalService terminalService, IProductService productService, TerminalProtocolStateManager terminalProtocolStateManager){
        return new DefaultTerminalAuthenticator(terminalService,productService,terminalProtocolStateManager);
    }
}
