package com.scene.mesh.engin.processor.then;

import com.scene.mesh.engin.model.OperationRequest;
import com.scene.mesh.engin.model.SceneMatchedResult;
import com.scene.mesh.foundation.api.processor.IProcessInput;
import com.scene.mesh.foundation.api.processor.IProcessOutput;
import com.scene.mesh.foundation.impl.processor.BaseProcessor;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.model.session.TerminalSession;
import com.scene.mesh.service.api.cache.MutableCacheService;
import com.scene.mesh.service.api.scene.ISceneService;
import com.scene.mesh.service.api.scene.SceneRelationType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.UUID;

public class SceneSelector extends BaseProcessor {

    @Getter
    @Setter
    private MutableCacheService cacheService;
    @Setter
    @Getter
    private ISceneService sceneService;
    @Setter
    @Getter
    private String env;

    @Override
    protected boolean process(Object inputObject, IProcessInput input, IProcessOutput output) throws Exception {
        //转换对象
        SceneMatchedResult sceneMatchedResult = (SceneMatchedResult) inputObject;
        //获取匹配的场景 ID
        String matchedSceneId = sceneMatchedResult.getSceneId();
        //获取终端 ID
        String terminalId = sceneMatchedResult.getTerminalId();
        //选择终端所处的场景
        Scene scene = selectSceneTerminalocated(terminalId, matchedSceneId);
        //更新终端 session
        boolean isSuccess = updateTerminalSession(terminalId, scene);
        if (isSuccess) {
            // TODO 封装真实 OperationRequest
            OperationRequest operationRequest = new OperationRequest();
            operationRequest.setProductId(scene.getProductId());
            operationRequest.setTerminalId(terminalId);
            operationRequest.setSceneId(scene.getId());
            operationRequest.setOperation(scene.getOperation());
            operationRequest.setEventsInScene(sceneMatchedResult.getMatchedEvents());
            output.getCollector().collect(operationRequest);
            return true;
        }
        return false;
    }

    private boolean updateTerminalSession(String terminalId, Scene scene) {
        TerminalSession ts = new TerminalSession();
        ts.setTerminalId(terminalId);
        ts.setSessionId(UUID.randomUUID().toString());
        ts.setProduceId(scene.getProductId());
        ts.setLocatedSceneId(scene.getId());
        return this.cacheService.updateTerminalSession(ts);
    }

    //选择终端所处的场景
    private Scene selectSceneTerminalocated(String terminalId, String matchedSceneId) {
        // 获取当前所处场景
        Scene matchedScene = this.sceneService.getSceneById(matchedSceneId);
        if (matchedScene == null) {
            throw new RuntimeException("SceneId:" + matchedSceneId + " 未发现");
        }

        return matchedScene;

//        TerminalSession currentSession = this.cacheService.getTerminalSessionByTerminalId(terminalId);
//        if (currentSession == null) { // 如果当前没有所处场景，直接返回匹配到的场景
//            return this.sceneService.getSceneById(matchedSceneId);
//        }
//        // 从当前场景和匹配到的场景中选择
//        Scene currentScene = this.cacheService.getSceneById(currentSession.getLocatedSceneId());
//        Scene matchedScene = this.cacheService.getSceneById(matchedSceneId);
//        //分析currentScene和matchedScene的关系
//        Pair<SceneRelationType, Scene> scenesRelation = this.sceneService.analyseScenesRelation(currentScene, matchedScene);
//        return scenesRelation.getRight();
    }
}
