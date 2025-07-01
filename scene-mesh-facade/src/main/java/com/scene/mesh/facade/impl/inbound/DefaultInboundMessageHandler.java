package com.scene.mesh.facade.impl.inbound;

import com.scene.mesh.facade.spec.common.IMessageExchanger;
import com.scene.mesh.facade.spec.inboud.InboundMessage;
import com.scene.mesh.facade.spec.inboud.InboundMessageHandler;
import com.scene.mesh.facade.spec.inboud.InboundMessageInterceptor;
import com.scene.mesh.facade.spec.outbound.OutboundMessage;
import com.scene.mesh.model.event.Event;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
public class DefaultInboundMessageHandler implements InboundMessageHandler{

    private final List<InboundMessageInterceptor> interceptors;

    private final IMessageExchanger messageExchanger;

    public DefaultInboundMessageHandler(List<InboundMessageInterceptor> interceptors,IMessageExchanger messageExchanger) {
        this.interceptors = interceptors;
        this.messageExchanger = messageExchanger;
    }

    @PostConstruct
    public void init() {
        this.interceptors.forEach(InboundMessageInterceptor::init);
    }

    public void handle(InboundMessage inboundMessage) {
        log.debug("Start processing the message - {}", inboundMessage.getMessage());

        //intercept request
        InboundMessageInterceptor.InboundMessageRequest request =
                new InboundMessageInterceptor.InboundMessageRequest(inboundMessage);

        //intercept response
        InboundMessageInterceptor.InboundMessageResponse response =
                new InboundMessageInterceptor.InboundMessageResponse();

        //intercept
        this.interceptMessage(request, response);

        //handle interception result - false
        if (!response.isSuccess()) {
            OutboundMessage outboundMessage = new OutboundMessage();
            outboundMessage.setTerminalId(request.getMessage().getTerminalId());
            outboundMessage.setMessage(response.getOpinion());

            this.messageExchanger.handleErrorOutboundMessage(outboundMessage);
            return;
        }

        //handle interception result - true
        Event event = (Event) response.getPropVal("event");
        this.messageExchanger.handleInboundEvent(event);
    }

    private void interceptMessage(InboundMessageInterceptor.InboundMessageRequest request, InboundMessageInterceptor.InboundMessageResponse response) {
        for (InboundMessageInterceptor interceptor : interceptors) {
            interceptor.intercept(request,response);
            if (!response.isSuccess()){
                break;
            }
        }
    }
}
