package com.scene.mesh.facade.impl.inbound;

import com.scene.mesh.foundation.spec.parameter.MetaParameters;
import com.scene.mesh.model.event.Event;
import com.scene.mesh.service.spec.event.IMetaEventService;

/**
 * json->event
 */
public class MessageToEventConvertor extends BaseInboundMessageInterceptor {

    @Override
    public void doIntercept(InboundMessageRequest request, InboundMessageResponse response) {
        MetaParameters metaParameters = (MetaParameters) response.getPropVal("metaParameters");

        // metaEventId
        String metaEventId = metaParameters.get("type",null); //metaEventId 经过 MessageLegalityChecker 拦截，不可能为空
        // terminalId
        String terminalId = request.getMessage().getTerminalId();

        Event event = new Event(metaEventId);
        event.setTerminalId(terminalId);

        //set payload of event
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
