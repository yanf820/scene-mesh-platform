package com.scene.mesh.service.spec.ai;

import com.scene.mesh.model.llm.LanguageModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;

/**
 * chat client 工厂
 */
public interface IChatClientFactory {

    ChatClient getChatClient(String providerName, String modelName);

//    ChatOptions getDefaultChatOptions(String providerName, String modelName);
}
