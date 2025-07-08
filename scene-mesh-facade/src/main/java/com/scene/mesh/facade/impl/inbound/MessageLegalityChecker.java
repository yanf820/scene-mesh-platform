package com.scene.mesh.facade.impl.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scene.mesh.facade.spec.inboud.InboundMessage;
import com.scene.mesh.foundation.spec.parameter.MetaParameters;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.foundation.impl.helper.StringHelper;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.service.spec.cache.MutableCacheService;
import com.scene.mesh.service.spec.event.IMetaEventService;

import java.util.Map;

public class MessageLegalityChecker extends BaseInboundMessageInterceptor {

    private final MutableCacheService mutableCacheService;

    public MessageLegalityChecker(MutableCacheService mutableCacheService) {
        this.mutableCacheService = mutableCacheService;
    }

    @Override
    protected void doIntercept(InboundMessageRequest request, InboundMessageResponse response) {

        InboundMessage inboundMessage = request.getMessage();

        // verify message
        String message = inboundMessage.getMessage();
        if (message == null || message.isEmpty()) {
            response.setSuccess(Boolean.FALSE);
            response.setOpinion("Illegal message - null");
            return;
        }

        MetaParameters metaParameters = null;
        try {
            metaParameters = new MetaParameters(message);
        } catch (JsonProcessingException e) {
            response.setSuccess(Boolean.FALSE);
            response.setOpinion(StringHelper.format("Illegal json message - {0}", e.getMessage()));
            return;
        }

        //verify metaEventId key
        String metaEventId = metaParameters.get("metaEventId", null);
        if (metaEventId == null || metaEventId.isEmpty()) {
            response.setSuccess(Boolean.FALSE);
            response.setOpinion("Illegal json message - metaEventId is missing");
            return;
        }

        //Verify the existence of the metaEvent
        IMetaEvent metaEvent = this.mutableCacheService.getIMetaEvent(metaEventId);
        if (metaEvent == null) {
            response.setSuccess(Boolean.FALSE);
            response.setOpinion(StringHelper.format("Illegal metaEventId - The metaEvent model cannot be found : metaEvent id {0}", metaEventId));
            return;
        }

        //Verify whether the payload conforms to the specification of the metaEvent model
        Map<String, Object> payload = metaParameters.getMap("payload");
        if (!metaEvent.validate(payload)) {
            response.setSuccess(Boolean.FALSE);
            response.setOpinion(StringHelper.format(
                    "Illegal payload - Does not conform to the specification of the metaEvent model. - payload: {0}, metaEvent: {1}",
                    SimpleObjectHelper.map2json(payload), SimpleObjectHelper.objectData2json(metaEvent)));
            return;
        }

        response.addPropEntry("metaParameters", metaParameters);
        response.setSuccess(Boolean.TRUE);
        response.setOpinion("json 校验完成");

    }

    @Override
    public String getName() {
        return "MessageLegalityChecker";
    }
}
