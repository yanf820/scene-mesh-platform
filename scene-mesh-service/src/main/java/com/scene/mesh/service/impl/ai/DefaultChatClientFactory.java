package com.scene.mesh.service.impl.ai;

import com.scene.mesh.foundation.impl.helper.StringHelper;
import com.scene.mesh.model.llm.LanguageModel;
import com.scene.mesh.model.llm.LanguageModelProvider;
import com.scene.mesh.service.impl.ai.model.openai.CompatibleOpenAiModel;
import com.scene.mesh.service.spec.ai.IChatClientFactory;
import com.scene.mesh.service.spec.ai.IChatModel;
import com.scene.mesh.service.spec.ai.ILLmConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DefaultChatClientFactory implements IChatClientFactory {

    private final ILLmConfigService llmConfigService;
    private final Map<Integer, ChatClient> chatClients;
//    private final Map<Integer, IChatModel> chatModels;


    public DefaultChatClientFactory(ILLmConfigService llmConfigService) {
        this.llmConfigService = llmConfigService;
        this.chatClients = new ConcurrentHashMap<>();
//        this.chatModels = new ConcurrentHashMap<>();
    }

    @Override
    public ChatClient getChatClient(String providerName, String modelName) {

        LanguageModelProvider lmp = this.llmConfigService.getLmpConfig(providerName);
        List<LanguageModel> llms = lmp.getModels();
        if (llms == null || llms.isEmpty()) {
            log.error("the llms of lmp is empty. lmp: {}, llm: {}", providerName, modelName);
            return null;
        }

        LanguageModel lm = null;
        for (LanguageModel llm : llms) {
            if (modelName.equals(llm.getName()))
                lm = llm;
        }

        if (lm == null) {
            log.error("can't find the model named:{} in the provider:{}", modelName, providerName);
            return null;
        }

        ChatClient chatClient = this.chatClients.get(lmp.hashCode() + lm.hashCode());
        if (chatClient != null) {
            return chatClient;
        }

        IChatModel chatModel = new CompatibleOpenAiModel(lm.getId(),lm.getName(), lmp.getName(), lmp.getApiHost(), lmp.getApiPath(), lmp.getApiKey());

        chatClient = ChatClient.builder(chatModel).build();

//        this.chatModels.put(lmp.hashCode() + lm.hashCode(), chatModel);
        this.chatClients.put(lmp.hashCode() + lm.hashCode(), chatClient);

        return chatClient;
    }

//    @Override
//    public ChatOptions getDefaultChatOptions(String providerName, String modelName) {
//
//        LanguageModelProvider lmp = this.llmConfigService.getLmpConfig(providerName);
//        List<LanguageModel> llms = lmp.getModels();
//        if (llms == null || llms.isEmpty()) {
//            log.error("the llms of lmp is empty. lmp: {}, llm: {}", providerName, modelName);
//            return null;
//        }
//
//        LanguageModel lm = null;
//        for (LanguageModel llm : llms) {
//            if (modelName.equals(llm.getName()))
//                lm = llm;
//        }
//
//        if (lm == null) {
//            log.error("can't find the model named:{} in the provider:{}", modelName, providerName);
//            return null;
//        }
//
//        IChatModel chatModel = this.chatModels.get(lmp.hashCode() + lm.hashCode());
//        if (chatModel == null) {
//            throw new RuntimeException(StringHelper.format(
//                    "Not found the chatModel. providerName:{0}, modelName:{1}",
//                    providerName, modelName));
//        }
//
//        return chatModel.getDefaultOptions();
//    }

//    public ChatClient getChatClient(LanguageModel languageModel) {
//
//        // 获取已存在的 chat client
//        ChatClient chatClient = this.chatClients.get(languageModel.getId());
//        if (chatClient != null) {
//            return chatClient;
//        }
//
//        // 没有则构建
//        chatClient = this.buildChatClient(languageModel).build();
//        this.chatClients.put(languageModel.getId(), chatClient);
//        return chatClient;
//    }
//    private ChatClient.Builder buildChatClient(LanguageModel languageModel) {
//        IChatModel chatModel = this.chatModels.get(languageModel.getId());
//
//        if (chatModel == null) {
//            throw new RuntimeException(StringHelper.format(
//                    "没有相关模型提供 - " + "provider:{0}, modelName:{1}, modelId:{2}",
//                    languageModel.getProvider(), languageModel.getName(), languageModel.getId()));
//        }
//
//        return ChatClient.builder(chatModel);
//    }

}
