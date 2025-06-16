package com.scene.mesh.engin.processor.process.operator;

import com.scene.mesh.engin.model.OperationRequest;
import com.scene.mesh.engin.model.OperationResponse;
import com.scene.mesh.model.operation.Operation;
import com.scene.mesh.model.session.TerminalSession;

public class NonAgentOperator implements IOperator {
    @Override
    public Operation.OperationType getOperationType() {
        return Operation.OperationType.NON_AGENT;
    }

    @Override
    public void process(OperationRequest operationRequest, OperationResponse operationResponse, TerminalSession session) {

    }
}
