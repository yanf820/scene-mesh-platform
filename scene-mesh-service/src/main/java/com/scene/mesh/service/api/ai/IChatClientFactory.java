package com.scene.mesh.service.api.ai;

import com.scene.mesh.model.llm.LanguageModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;

/**
 * chat client 工厂
 */
public interface IChatClientFactory {

    /**
     * 根据 languageModel 获取 chat client
     * @param languageModel
     * @return
     */
    ChatClient getChatClient(LanguageModel languageModel);

    ChatOptions getDefaultChatOptions(LanguageModel llm);
}
