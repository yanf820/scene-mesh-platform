package com.scene.mesh.facade.impl.inbound;

import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptorCollection;
import com.scene.mesh.foundation.spec.parameter.data.calculate.IParameterCalculateType;
import com.scene.mesh.model.event.Event;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.service.spec.event.IMetaEventService;

import java.util.Map;

import static com.googlecode.aviator.runtime.type.AviatorRuntimeJavaElementType.ContainerType.List;

public class ComputableFieldCalculator extends BaseInboundMessageInterceptor {

    private final IMetaEventService metaEventService;

    public ComputableFieldCalculator(IMetaEventService metaEventService) {
        this.metaEventService = metaEventService;
    }

    @Override
    protected void doIntercept(InboundMessageRequest request, InboundMessageResponse response) {
        Event event = (Event) response.getPropVal("event");
        String metaEventId = event.getMetaEventId();
        IMetaEvent metaEvent = this.metaEventService.getIMetaEvent(metaEventId);
        Map<String,Object> payload = event.getPayload();

        MetaParameterDescriptorCollection collection = metaEvent.getParameterCollection();
        collection.getParameterDescriptors().forEach(parameterDescriptor -> {
            IParameterCalculateType calculateType = parameterDescriptor.getCalculateType();
            if (calculateType != null){
                if (IParameterCalculateType.CalculateType.STT.equals(calculateType)) {
                    String[] sourceFieldName = {};
                    calculateType.(payload,parameterDescriptor);
                }
            }
        });
        event.setPayload(payload);
    }

    @Override
    public String getName() {
        return "ComputableFieldCalculator";
    }
}
