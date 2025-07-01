package com.scene.mesh.engin.generator;

import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptor;
import com.scene.mesh.foundation.spec.parameter.data.StringParameterDataType;
import com.scene.mesh.model.action.DefaultMetaAction;
import com.scene.mesh.model.action.IMetaAction;
import com.scene.mesh.model.event.Event;
import com.scene.mesh.service.impl.ai.template.AviatorTemplateRenderer;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PromptGenerator {

    public static void main(String[] args) {
        Event voiceE = new Event("voice");
        voiceE.setTerminalId("terminal-1");
        voiceE.addPayloadEntry("text","你好啊");
        voiceE.addPayloadEntry("volume","1");

        Event faceE = new Event("face");
        faceE.setTerminalId("terminal-1");
        faceE.addPayloadEntry("expression","愉快");

        List<Event> events = new ArrayList<>();
        events.add(voiceE);
        events.add(faceE);


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


        // 从 classpath 加载模板文件
        Resource templateResource = new ClassPathResource("user_prompt_template.av");
        System.out.println("-> 成功加载模板: " + templateResource.getFilename());

        // 将所有数据放入 Map，键名与 .av 文件中使用的变量名一致
        Map<String, Object> variables = Map.of(
                "events", events,
                "metaActions", metaActions,
                "productId", "product-1",
                "terminalId", "terminal-1"
        );

        PromptTemplate template = PromptTemplate.builder()
                .resource(templateResource)
                .variables(variables)
                .renderer(new AviatorTemplateRenderer())
                .build();

        Prompt prompt = template.create();
        System.out.println(prompt.getContents());
    }
}
