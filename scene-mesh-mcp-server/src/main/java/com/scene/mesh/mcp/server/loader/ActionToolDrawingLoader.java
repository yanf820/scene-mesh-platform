package com.scene.mesh.mcp.server.loader;

import com.scene.mesh.foundation.spec.parameter.IMetaParameterized;
import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptor;
import com.scene.mesh.foundation.spec.parameter.data.StringParameterDataType;
import com.scene.mesh.model.action.DefaultMetaAction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ActionToolDrawingLoader implements IToolDrawingLoader {

    @Override
    public List<IMetaParameterized> loadToolDrawings() {

        DefaultMetaAction voiceMetaAction = new DefaultMetaAction(
                "voice_action",
                "语音播放动作",
                "控制设备播放语音内容，用于与用户进行语音交互、朗读文本、播放提示音等场景。当需要设备说话、朗读、播报消息时使用此工具","bry9wbiy3nasmd8afkkf0wjm"
        );
        voiceMetaAction.addParameterDescriptor(new MetaParameterDescriptor(
                "text",
                "对话文本",
                "需要播放的文本内容，支持中英文，不能为空。例如：'你好，欢迎使用智能助手'、'今天天气很好'",
                new StringParameterDataType(),
                true
        ));
        voiceMetaAction.addParameterDescriptor(new MetaParameterDescriptor(
                "volume",
                "音量",
                "播放音量，取值范围0-100的数字字符串，不填则使用默认音量50。例如：'80'表示80%音量，'30'表示较小音量",
                new StringParameterDataType(),
                false
        ));

        DefaultMetaAction animationMetaAction = new DefaultMetaAction(
                "face_action",
                "表情播放动作",
                "控制设备显示表情动画，用于表达情感状态，增强人机交互的亲和力。当需要表达高兴、悲伤等情绪，或配合语音内容展示合适表情时使用"
        ,"bry9wbiy3nasmd8afkkf0wjm");
        animationMetaAction.addParameterDescriptor(new MetaParameterDescriptor(
                "face_type",
                "表情类型",
                "要播放的表情类型，必须选择以下数字之一：'1'-开心/高兴（成功、赞美时），'2'-伤心/难过（遗憾、失望时），'3'-沮丧/失望（挫败、无奈时），'4'-调皮/俏皮（幽默、玩笑时），'5'-无所事事/无聊（等待、思考时）",
                new StringParameterDataType(),
                true
        ));

        DefaultMetaAction rotateMetaAction = new DefaultMetaAction(
                "rotate_action",
                "转身动作",
                "控制设备身体旋转，用于调整朝向、寻找目标、表达动作意图。当需要设备转向特定方向、回正朝向、或表达转身动作时使用"
        ,"bry9wbiy3nasmd8afkkf0wjm");
        rotateMetaAction.addParameterDescriptor(new MetaParameterDescriptor(
                "rotate_type",
                "转动类型",
                "旋转方向类型，必须选择以下数字之一：'0'-回到原始正面朝向，'1'-向左转（逆时针），'2'-向右转（顺时针）",
                new StringParameterDataType(),
                true
        ));
        rotateMetaAction.addParameterDescriptor(new MetaParameterDescriptor(
                "rotate_angle",
                "转身角度",
                "旋转的角度数值，取值范围1-180的整数字符串，单位为度。常用角度：'15'（小幅转动），'45'（中等转动），'90'（直角转动），'180'（转向背面）",
                new StringParameterDataType(),
                true
        ));

        List<IMetaParameterized> metaActions = new ArrayList<>();
        metaActions.add(voiceMetaAction);
        metaActions.add(animationMetaAction);
        metaActions.add(rotateMetaAction);

        return metaActions;
    }
}
