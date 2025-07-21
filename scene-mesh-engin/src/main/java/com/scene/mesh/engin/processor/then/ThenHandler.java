package com.scene.mesh.engin.processor.then;

import com.scene.mesh.engin.model.ThenRequest;
import com.scene.mesh.engin.processor.then.operator.IThenOperator;
import com.scene.mesh.engin.processor.then.operator.ThenOperatorManager;
import com.scene.mesh.foundation.spec.processor.IProcessActivateContext;
import com.scene.mesh.foundation.spec.processor.IProcessInput;
import com.scene.mesh.foundation.spec.processor.IProcessOutput;
import com.scene.mesh.foundation.impl.processor.BaseProcessor;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.model.scene.WhenThen;
import com.scene.mesh.service.spec.scene.ISceneService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ThenHandler extends BaseProcessor {

    private ISceneService sceneService;

    private ThenOperatorManager operatorManager;

    public ThenHandler(ISceneService sceneService, ThenOperatorManager operatorManager) {
        this.sceneService = sceneService;
        this.operatorManager = operatorManager;
    }

    @Override
    public void activate(IProcessActivateContext activateContext) throws Exception {
        super.activate(activateContext);
    }

    @Override
    protected boolean process(Object inputObject, IProcessInput input, IProcessOutput output) throws Exception {
        ThenRequest thenRequest = (ThenRequest) inputObject;
        String terminalId = thenRequest.getTerminalId();
        String sceneId = thenRequest.getSceneId();
        String thenId = thenRequest.getThenId();

        Scene scene = this.sceneService.getSceneById(sceneId);
        if (scene == null) {
            log.error("not found scene when process thenRequest. sceneId:{}, thenId:{} ",
                    sceneId, thenId);
            return false;
        }
        List<WhenThen> whenThens = scene.getWhenThenList();
        if (whenThens == null || whenThens.isEmpty()) {
            log.error("not found 'whenThenList' when process thenRequest. sceneId:{}, thenId:{} ",
                    sceneId, thenId);
            return false;
        }

        WhenThen.Then then = null;
        for (WhenThen whenThen : whenThens) {
            String tid = whenThen.getThen().getId();
            if (thenId.equals(tid)) {
                then = whenThen.getThen();
            }
        }
        if (then == null) {
            log.error("not found 'then' when process thenRequest. sceneId:{}, thenId:{} ",
                    sceneId, thenId);
            return false;
        }

        IThenOperator thenOperator = this.operatorManager.getOperator(then.getType());

        if (thenOperator == null) {
            log.error("not found 'thenOperator' when process thenRequest. sceneId:{}, thenId:{}, thenType:{} ",
                    sceneId, thenId ,then.getType());
            return false;
        }

        return thenOperator.process(terminalId,scene,then,thenRequest.getEventsInScene(),output);

//        Terminalthis.terminalService.getTerminalWithTerminalId(terminalId);
//
//        this.operatorManager.getOperator();
//
//        IThe operation = thenRequest.getOperation();
//        Operation.OperationType operationType = operation.getOperationType();
//        IOperator operator = this.operatorManager.getOperator(operationType);
//        if (operator == null) {
//            log.error("未找到对应的 operator - operationType: {}", operationType);
//            return false;
//        }
//        OperationResponse operationResponse = new OperationResponse();
//        TerminalSession session = this.cacheService.getTerminalSessionByTerminalId(operationRequest.getTerminalId());
//
//        operator.process(operationRequest,operationResponse,session);
//
//        output.getCollector().collect(operationResponse);
//        return true;
    }

}
