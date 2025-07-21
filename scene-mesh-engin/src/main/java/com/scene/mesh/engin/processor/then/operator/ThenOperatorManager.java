package com.scene.mesh.engin.processor.then.operator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThenOperatorManager {

    private final Map<String, IThenOperator> operators;

    public ThenOperatorManager(List<IThenOperator> operators) {
        this.operators = new ConcurrentHashMap<>();
        for (IThenOperator operator : operators) {
            this.registerOperator(operator);
        }
    }

    public void registerOperator(IThenOperator operator) {
        this.operators.put(operator.getOperatorType(), operator);
    }

    public IThenOperator getOperator(String operationType) {
        return this.operators.get(operationType);
    }
}
