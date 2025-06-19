package com.scene.mesh.engin.processor.then.operator;

import com.scene.mesh.model.operation.Operation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OperatorManager {

    private Map<Operation.OperationType, IOperator> operators;

    public OperatorManager(List<IOperator> operators) {
        this.operators = new ConcurrentHashMap<>();
        for (IOperator operator : operators) {
            this.registerOperator(operator);
        }
    }

    public void registerOperator(IOperator operator) {
        this.operators.put(operator.getOperationType(), operator);
    }

    public IOperator getOperator(Operation.OperationType operationType) {
        return this.operators.get(operationType);
    }
}
