package com.scene.mesh.engin.processor.process.operator;

import com.scene.mesh.engin.model.OperationRequest;
import com.scene.mesh.engin.model.OperationResponse;
import com.scene.mesh.model.operation.Operation;
import com.scene.mesh.model.session.TerminalSession;

public interface IOperator {

    Operation.OperationType getOperationType();

    void process(OperationRequest operationRequest, OperationResponse operationResponse, TerminalSession session);
}