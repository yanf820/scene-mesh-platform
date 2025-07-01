package com.scene.mesh.service.impl.ai;

import com.scene.mesh.foundation.impl.helper.StringHelper;
import com.scene.mesh.model.llm.LanguageModel;
import com.scene.mesh.service.spec.ai.IChatClientFactory;
import com.scene.mesh.service.spec.ai.IChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultChatClientFactory implements IChatClientFactory {

    private Map<String, IChatModel> chatModels;
    private Map<String, ChatClient> chatClients;


    public DefaultChatClientFactory(List<IChatModel> chatModels) {
        this.chatClients = new ConcurrentHashMap<>();
        this.chatModels = new ConcurrentHashMap<>();
        for (IChatModel chatModel : chatModels) {
            this.chatModels.put(chatModel.getModelId(), chatModel);
        }
    }

    @Override
    public ChatClient getChatClient(LanguageModel languageModel) {

        // 获取已存在的 chat client
        ChatClient chatClient = this.chatClients.get(languageModel.getId());
        if (chatClient != null) {
            return chatClient;
        }

        // 没有则构建
        chatClient = this.buildChatClient(languageModel).build();
        this.chatClients.put(languageModel.getId(), chatClient);
        return chatClient;
    }

    @Override
    public ChatOptions getDefaultChatOptions(LanguageModel languageModel) {
        IChatModel chatModel = this.chatModels.get(languageModel.getId());
        if (chatModel == null) {
            throw new RuntimeException(StringHelper.format(
                    "没有相关模型提供 - " + "provider:{0}, modelName:{1}, modelId:{2}",
                    languageModel.getProvider(), languageModel.getName(), languageModel.getId()));
        }

        return chatModel.getDefaultOptions();
    }

    private ChatClient.Builder buildChatClient(LanguageModel languageModel) {
        IChatModel chatModel = this.chatModels.get(languageModel.getId());

        if (chatModel == null) {
            throw new RuntimeException(StringHelper.format(
                    "没有相关模型提供 - " + "provider:{0}, modelName:{1}, modelId:{2}",
                    languageModel.getProvider(), languageModel.getName(), languageModel.getId()));
        }

        return ChatClient.builder(chatModel);
    }
}
