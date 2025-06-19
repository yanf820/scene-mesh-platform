package com.scene.mesh.model.scene;

import com.scene.mesh.foundation.api.parameter.MetaParameterDescriptor;
import com.scene.mesh.foundation.api.parameter.data.StringParameterDataType;
import com.scene.mesh.model.action.DefaultMetaAction;
import com.scene.mesh.model.action.IMetaAction;
import com.scene.mesh.model.llm.LanguageModel;
import com.scene.mesh.model.llm.LanguageModelProvider;
import com.scene.mesh.model.operation.Agent;
import com.scene.mesh.model.operation.Operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockSceneRepository {

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
        agent1.setToolNames(Arrays.asList("search_knowledge", "create_ticket"));
        agent1.setKnowledgeBaseIds(Arrays.asList("kb-customer-service", "kb-product-info"));
        agent1.setMetaActions(this.mockActions());
        
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

    private List<IMetaAction> mockActions(){
         DefaultMetaAction voiceMetaAction = new DefaultMetaAction("voice_action","语音播放动作","语音播放动作");
         voiceMetaAction.addParameterDescriptor(new MetaParameterDescriptor("text","对话文本","",new StringParameterDataType(),true));
         voiceMetaAction.addParameterDescriptor(new MetaParameterDescriptor("volume","音量","",new StringParameterDataType(),false));

         DefaultMetaAction animationMetaAction = new DefaultMetaAction("face_action","表情播放动作","表情播放动作");
         animationMetaAction.addParameterDescriptor(new MetaParameterDescriptor("face_type","表情类型","表情类型, ‘1’-开心, ‘2’-伤心, ‘3’-沮丧, ‘4’-调皮, ‘5’-无所事事",new StringParameterDataType(),true));

         DefaultMetaAction rotateMetaAction = new DefaultMetaAction("rotate_action","转身动作","设备身体转动");
         rotateMetaAction.addParameterDescriptor(new MetaParameterDescriptor("rotate_type","转动类型","转动类型: ‘0’- 回到原始方向, ‘1’-向左转, ‘2’-向右转",new StringParameterDataType(),true));
         rotateMetaAction.addParameterDescriptor(new MetaParameterDescriptor("rotate_angle","转身角度","转身角度, 例如'15',是指旋转15度",new StringParameterDataType(),true));

         List<IMetaAction> metaActions = new ArrayList<>();
         metaActions.add(voiceMetaAction);
         metaActions.add(animationMetaAction);
         metaActions.add(rotateMetaAction);

         return metaActions;
    }
}
