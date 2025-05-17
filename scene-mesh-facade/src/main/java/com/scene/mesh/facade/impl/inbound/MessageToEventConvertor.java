package com.scene.mesh.facade.impl.inbound;

import com.scene.mesh.facade.api.inboud.InboundMessageInterceptor;
import com.scene.mesh.foundation.api.parameter.MetaParameters;
import com.scene.mesh.model.event.Event;
import com.scene.mesh.service.api.cache.MutableCacheService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * json->事件转换器
 */
public class MessageToEventConvertor extends BaseInboundMessageInterceptor {

    private MutableCacheService mutableCacheService;

    public MessageToEventConvertor(MutableCacheService mutableCacheService) {
        this.mutableCacheService = mutableCacheService;
    }

    @Override
    public void doIntercept(InboundMessageRequest request, InboundMessageResponse response) {
        MetaParameters metaParameters = (MetaParameters) response.getPayloadVal("metaParameters");

        // metaEventId
        String metaEventId = request.getMetaEventId(); //metaEventId 经过 MessageLegalityChecker 拦截，不可能为空
        // clientId
        String clientId = request.getClientId();
        // tenantId
        String tenantId = "official";
        // productId
        String productId = "supply chain";

        Event event = new Event(metaEventId);
        event.setTenantId(tenantId);
        event.setProductId(productId);
        event.setTerminalId(clientId);
        event.setProtocolSessionId(request.getProtocolSessionId());
        //把json 加入 event
        event.setPayload(metaParameters.getParameterMap());

        response.setSuccess(true);
        response.setSourceMessage(request.getMessage());
        response.addPayloadEntry("event",event);
    }

    @Override
    public void init() {

    }

    @Override
    public String getName() {
        return "JsonToEventConvertor";
    }
}
