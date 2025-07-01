package com.scene.mesh.engin.processor.then;

import com.scene.mesh.engin.model.OperationRequest;
import com.scene.mesh.engin.model.OperationResponse;
import com.scene.mesh.engin.processor.then.operator.IOperator;
import com.scene.mesh.engin.processor.then.operator.OperatorManager;
import com.scene.mesh.foundation.spec.processor.IProcessActivateContext;
import com.scene.mesh.foundation.spec.processor.IProcessInput;
import com.scene.mesh.foundation.spec.processor.IProcessOutput;
import com.scene.mesh.foundation.impl.processor.BaseProcessor;
import com.scene.mesh.model.operation.Operation;
import com.scene.mesh.model.session.TerminalSession;
import com.scene.mesh.service.spec.cache.MutableCacheService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OperationHandler extends BaseProcessor {

    private MutableCacheService cacheService;

    private OperatorManager operatorManager;

    public OperationHandler(MutableCacheService cacheService, OperatorManager operatorManager) {
        this.cacheService = cacheService;
        this.operatorManager = operatorManager;
    }

    @Override
    public void activate(IProcessActivateContext activateContext) throws Exception {
        super.activate(activateContext);
    }

    @Override
    protected boolean process(Object inputObject, IProcessInput input, IProcessOutput output) throws Exception {
        OperationRequest operationRequest = (OperationRequest) inputObject;
        Operation operation = operationRequest.getOperation();
        Operation.OperationType operationType = operation.getOperationType();
        IOperator operator = this.operatorManager.getOperator(operationType);
        if (operator == null) {
            log.error("未找到对应的 operator - operationType: {}", operationType);
            return false;
        }
        OperationResponse operationResponse = new OperationResponse();
        TerminalSession session = this.cacheService.getTerminalSessionByTerminalId(operationRequest.getTerminalId());

        operator.process(operationRequest,operationResponse,session);

        output.getCollector().collect(operationResponse);
        return true;
    }

}
