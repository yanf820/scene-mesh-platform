package com.scene.mesh.facade.impl.inbound;

import com.scene.mesh.facade.api.common.IMessageExchanger;
import com.scene.mesh.facade.api.inboud.InboundMessage;
import com.scene.mesh.facade.api.inboud.InboundMessageHandler;
import com.scene.mesh.facade.api.inboud.InboundMessageInterceptor;
import com.scene.mesh.facade.api.outbound.OutboundMessage;
import com.scene.mesh.model.event.Event;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * 终端消息处理服务
 */
@Slf4j
@Component
public class DefaultInboundMessageHandler implements InboundMessageHandler{

    List<InboundMessageInterceptor> interceptors = new LinkedList<>();

    @Autowired
    private IMessageExchanger messageExchanger;

    public DefaultInboundMessageHandler(List<InboundMessageInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    @PostConstruct
    public void init() {
        this.interceptors.forEach(InboundMessageInterceptor::init);
    }

    public void handle(InboundMessage inboundMessage) {
        log.info("开始处理消息 - {}", inboundMessage.getMessage());

        //定义 intercept request
        InboundMessageInterceptor.InboundMessageRequest request =
                new InboundMessageInterceptor.InboundMessageRequest(inboundMessage);

        //定义 intercept response
        InboundMessageInterceptor.InboundMessageResponse response =
                new InboundMessageInterceptor.InboundMessageResponse();

        //拦截消息处理
        this.interceptMessage(request, response);

        //拦截处理结果false 的情况，交给事件监听器进行 error 处理
        if (!response.isSuccess()) {
            OutboundMessage outboundMessage = new OutboundMessage();
            outboundMessage.setTerminalId(request.getMessage().getTerminalId());
            outboundMessage.setMessage(response.getOpinion());

            this.messageExchanger.handleErrorOutboundMessage(outboundMessage);
            return;
        }

        //拦截处理结果true 的情况，进行 入站 处理
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
