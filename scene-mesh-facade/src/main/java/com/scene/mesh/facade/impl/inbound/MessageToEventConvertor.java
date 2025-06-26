package com.scene.mesh.facade.impl.inbound;

import com.scene.mesh.foundation.api.parameter.MetaParameters;
import com.scene.mesh.model.event.Event;

/**
 * json->事件转换器
 */
public class MessageToEventConvertor extends BaseInboundMessageInterceptor {

    @Override
    public void doIntercept(InboundMessageRequest request, InboundMessageResponse response) {
        MetaParameters metaParameters = (MetaParameters) response.getPropVal("metaParameters");

        // metaEventId
        String metaEventId = metaParameters.get("metaEventId",null); //metaEventId 经过 MessageLegalityChecker 拦截，不可能为空
        // terminalId
        String terminalId = request.getMessage().getTerminalId();

        Event event = new Event(metaEventId);
        event.setTerminalId(terminalId);

        //把json 加入 event
        event.setPayload(metaParameters.getMap("payload"));

        response.setSuccess(true);
        response.setSourceMessage(request.getMessage().getMessage());
        response.addPropEntry("event",event);
    }

    @Override
    public String getName() {
        return "JsonToEventConvertor";
    }
}
