package com.scene.mesh.facade.impl.inbound;

import com.scene.mesh.facade.spec.inboud.InboundMessageInterceptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseInboundMessageInterceptor implements InboundMessageInterceptor {

    @Override
    public void intercept(InboundMessageRequest request, InboundMessageResponse response) {
        log.debug("The interceptor {} intercept item - terminalId: {}, message: {}",
                getName(), request.getMessage().getTerminalId(),request.getMessage());
        doIntercept(request,response);
        if (!response.isSuccess()){
            return;
        }
        log.debug("Intercept {} response - {}", getName(), response.toString());
    }

    protected abstract void doIntercept(InboundMessageRequest request, InboundMessageResponse response);

    @Override
    public void init() {

    }
}
