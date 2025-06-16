package com.scene.mesh.engin.processor.process;

import com.scene.mesh.engin.model.OperationRequest;
import com.scene.mesh.engin.model.OperationResponse;
import com.scene.mesh.engin.processor.process.operator.AgentOperator;
import com.scene.mesh.engin.processor.process.operator.IOperator;
import com.scene.mesh.engin.processor.process.operator.NonAgentOperator;
import com.scene.mesh.engin.processor.process.operator.OperatorManager;
import com.scene.mesh.foundation.api.processor.IProcessActivateContext;
import com.scene.mesh.foundation.api.processor.IProcessInput;
import com.scene.mesh.foundation.api.processor.IProcessOutput;
import com.scene.mesh.foundation.impl.processor.BaseProcessor;
import com.scene.mesh.model.operation.Operation;
import com.scene.mesh.model.session.TerminalSession;
import com.scene.mesh.service.api.cache.MutableCacheService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OperationHandler extends BaseProcessor {

    private MutableCacheService cacheService;

    private OperatorManager operatorManager;

    public OperationHandler(MutableCacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public void activate(IProcessActivateContext activateContext) throws Exception {
        super.activate(activateContext);
        this.operatorManager = new OperatorManager();
        this.operatorManager.registerOperator(new AgentOperator());
        this.operatorManager.registerOperator(new NonAgentOperator());
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
