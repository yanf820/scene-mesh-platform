package com.scene.mesh.service.impl.ai.model.zhipu;

import com.scene.mesh.service.api.ai.IChatModel;
import com.scene.mesh.service.impl.ai.model.ChatModelD;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;

public class ZhiPuChatModel implements IChatModel {

    private final ChatModel chatModel;

    public ZhiPuChatModel(String apiKey) {
        ZhiPuAiApi zhiPuAiApi =
                new ZhiPuAiApi(apiKey);
        chatModel = new ZhiPuAiChatModel(zhiPuAiApi,
                ZhiPuAiChatOptions
                        .builder()
                        .model(ZhiPuAiApi.ChatModel.GLM_4_Flash.getValue())
                        .build()
        );
    }

    @Override
    public String getModelId() {
        return ChatModelD.ZhiPu_GLM_4_Flash.name();
    }

    @Override
    public String getModelName() {
        return "智谱 GLM_4_flash 免费模型";
    }

    @Override
    public String getProvider() {
        return "智谱";
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        return this.chatModel.call(prompt);
    }
}
