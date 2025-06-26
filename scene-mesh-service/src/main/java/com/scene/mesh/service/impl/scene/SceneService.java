package com.scene.mesh.service.impl.scene;

import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.model.llm.LanguageModel;
import com.scene.mesh.model.llm.LanguageModelProvider;
import com.scene.mesh.model.operation.Agent;
import com.scene.mesh.model.operation.Operation;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.service.api.scene.ISceneService;
import com.scene.mesh.service.api.scene.SceneRelationType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SceneService implements ISceneService {
    @Override
    public Scene getSceneById(String sceneId) {

        List<Scene> scenes = getAllScenes();
        for (Scene scene : scenes) {
            if (scene.getId().equals(sceneId))
                return scene;
        }

        return null;
    }

    public List<Scene> getAllScenes(){
        List<Scene> scenes = new ArrayList<>();

        // 创建第一个场景 - 智能客服场景
        Scene scene1 = new Scene();
        scene1.setId("1");
        scene1.setProductId("product-001");
        scene1.setName("早教场景");
        scene1.setDescription("提供早教类服务");
        scene1.setEnable(true);
//        scene1.setRules("{\"triggers\":[{\"eventType\":\"USER_MESSAGE\",\"conditions\":[{\"field\":\"content\",\"operator\":\"contains\",\"value\":\"咨询\"}]}],\"actions\":[{\"type\":\"CALL_AGENT\",\"agentId\":\"customer-service-agent\"}]}");
        scene1.setPriority(1);

        // 创建智能客服Agent
        Agent agent1 = new Agent();
        agent1.setId("ee-agent");

        LanguageModelProvider provider1 = new LanguageModelProvider();
        provider1.setId("zhipu_ai");
        provider1.setName("智谱 AI");
        provider1.setDescription("智谱AI提供多个大模型");
//        provider1.setApiMode("rest");
//        provider1.setApiHost("https://api.openai.com");
//        provider1.setApiCompatibility(true);

        LanguageModel lm1 = new LanguageModel();
        lm1.setId("ZhiPu_GLM_4_Flash");
        lm1.setName("zhipu_glm_4_Flash");
//        lm1.setDescription("OpenAI GPT-4o模型，具有强大的对话能力");
        lm1.setProvider(provider1);
        agent1.setLanguageModel(lm1);

        agent1.setScenePrompt("你是一个早教老师，请礼貌地回答用户的问题，并提供有用的帮助。");
        agent1.setToolNames(Arrays.asList("bing_search", "voice_action","face_action","rotate_action"));
        agent1.setKnowledgeBaseIds(Arrays.asList("kb-customer-service", "kb-product-info"));

        Operation operation1 = new Operation();
        operation1.setOperationType(Operation.OperationType.AGENT);
        operation1.setAgent(agent1);
        scene1.setOperation(operation1);

        // 创建第二个场景 - 订单处理场景
        Scene scene3 = new Scene();
        scene3.setId("3");
        scene3.setProductId("product-002");
        scene3.setName("订单处理场景");
        scene3.setDescription("自动处理用户订单，包括订单确认、支付处理、物流跟踪等");
        scene3.setEnable(true);
        scene3.setRules("{\"triggers\":[{\"eventType\":\"ORDER_CREATED\",\"conditions\":[{\"field\":\"status\",\"operator\":\"equals\",\"value\":\"pending\"}]}],\"actions\":[{\"type\":\"NON_AGENT\",\"workflow\":\"order-processing-workflow\"}]}");
        scene3.setPriority(3);

        // 创建非Agent操作
        Operation operation3 = new Operation();
        operation3.setOperationType(Operation.OperationType.NON_AGENT);
        scene3.setOperation(operation3);

        scenes.add(scene1);
        scenes.add(scene3);

        return scenes;
    }

//    @Override
//    public Pair<SceneRelationType,Scene> analyseScenesRelation(Scene currentScene, Scene matchedScene){
//        if (currentScene == null || matchedScene == null) {
//            throw new IllegalArgumentException("空场景无法进行关系分析. currentScene=" + SimpleObjectHelper.objectData2json(currentScene)
//                    + ", matchedScene=" + SimpleObjectHelper.objectData2json(matchedScene));
//        }
//
//        //相同
//        if (currentScene.getId().equals(matchedScene.getId())) {
//            return Pair.of(SceneRelationType.SAME, currentScene);
//        }
//
//        LinkedList<Scene> currentParentOfScene1 = this.cacheService.getParentScenesById(currentScene.getId());
//        LinkedList<Scene> matchedParentOfScene2 = this.cacheService.getParentScenesById(matchedScene.getId());
//
//        //直系关系，选择叶子场景
//        for (Scene ps1 : currentParentOfScene1) {
//            if (ps1.getId().equals(matchedScene.getId()))
//                return Pair.of(SceneRelationType.LINEAL,currentScene);
//        }
//
//        for (Scene ps2 : matchedParentOfScene2) {
//            if (ps2.getId().equals(currentScene.getId()))
//                return Pair.of(SceneRelationType.LINEAL,matchedScene);
//        }
//
//        //兄弟关系，选择优先级高的场景
//        if (currentParentOfScene1.getLast().getId().equals(matchedParentOfScene2.getLast().getId())){
//            if (currentScene.getPriority() > matchedScene.getPriority())
//                return Pair.of(SceneRelationType.SIBLING,currentScene);
//            else
//                return Pair.of(SceneRelationType.SIBLING,matchedScene);
//        }
//
//        //未知关系，选择匹配到的场景
//        return Pair.of(SceneRelationType.UNKNOWN,matchedScene);
//    }

}
