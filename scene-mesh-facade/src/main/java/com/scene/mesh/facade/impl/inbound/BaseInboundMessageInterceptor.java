package com.scene.mesh.facade.impl.inbound;

import com.scene.mesh.facade.api.inboud.InboundMessageInterceptor;
import lombok.extern.slf4j.Slf4j;

/**
 * inbound 拦截基类
 */
@Slf4j
public abstract class BaseInboundMessageInterceptor implements InboundMessageInterceptor {

    @Override
    public void intercept(InboundMessageRequest request, InboundMessageResponse response) {
        log.info("入站拦截器 {} 开始工作 - clientId: {}, message: {}, protocol: {}",
                getName(), request.getClientId(),request.getMessage(),request.getProtocolType());
        doIntercept(request,response);
        log.info("入站拦截器 {} 完成工作.",getName());
    }

    protected abstract void doIntercept(InboundMessageRequest request, InboundMessageResponse response);

    @Override
    public void init() {

    }
}
