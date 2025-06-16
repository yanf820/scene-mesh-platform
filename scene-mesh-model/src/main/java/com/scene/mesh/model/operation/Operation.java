package com.scene.mesh.model.operation;

import com.scene.mesh.model.action.IMetaAction;
import lombok.Data;

import java.util.List;

/**
 * 场景 Operation
 */
@Data
public class Operation {

    private OperationType operationType;

    private Agent agent;

    public enum OperationType {
        AGENT, // 通过 agent 进行 operation 处理
        NON_AGENT, // 非 agent 进行 operation 处理
    }
}
