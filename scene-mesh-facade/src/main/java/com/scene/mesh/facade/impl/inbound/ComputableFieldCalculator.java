package com.scene.mesh.facade.impl.inbound;

import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptor;
import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptorCollection;
import com.scene.mesh.foundation.spec.parameter.data.calculate.IParameterCalculator;
import com.scene.mesh.foundation.spec.parameter.data.calculate.IParameterCalculatorManager;
import com.scene.mesh.model.event.Event;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.service.spec.event.IMetaEventService;

import java.util.Map;

public class ComputableFieldCalculator extends BaseInboundMessageInterceptor {

    private final IMetaEventService metaEventService;

    private final IParameterCalculatorManager parameterCalculatorManager;

    public ComputableFieldCalculator(IMetaEventService metaEventService, IParameterCalculatorManager parameterCalculatorManager) {
        this.metaEventService = metaEventService;
        this.parameterCalculatorManager = parameterCalculatorManager;
    }

    @Override
    protected void doIntercept(InboundMessageRequest request, InboundMessageResponse response) {
        Event event = (Event) response.getPropVal("event");
        String metaEventId = event.getType();
        IMetaEvent metaEvent = this.metaEventService.getIMetaEvent(metaEventId);
        Map<String, Object> payload = event.getPayload();

        MetaParameterDescriptorCollection collection = metaEvent.getParameterCollection();
        for (MetaParameterDescriptor parameterDescriptor : collection.getParameterDescriptors()) {
            IParameterCalculator.CalculateType calculateType = parameterDescriptor.getCalculateType();
            if (calculateType == null) continue;

            IParameterCalculator calculator = this.parameterCalculatorManager.getParameterCalculator(calculateType);
            if (calculator == null) {
                throw new RuntimeException("Can not find calculator for " + calculateType);
            }

            calculator.calculate(event.getTerminalId(), event.getPayload(), parameterDescriptor);

        }

        event.setPayload(payload);
    }

    @Override
    public String getName() {
        return "ComputableFieldCalculator";
    }
}
