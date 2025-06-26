package com.scene.mesh.service.impl.ai;

import com.scene.mesh.model.action.IMetaAction;
import com.scene.mesh.model.event.Event;
import com.scene.mesh.model.llm.LanguageModel;
import com.scene.mesh.model.operation.Agent;
import com.scene.mesh.service.api.ai.IAgentService;
import com.scene.mesh.service.api.ai.IChatClientFactory;
import com.scene.mesh.service.api.ai.IPromptService;
import com.scene.mesh.service.api.ai.IToolsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DefaultAgentService implements IAgentService {

    private IChatClientFactory chatClientFactory;

    private IPromptService promptService;

    private IToolsService toolsService;

    private final Resource templateResource = new ClassPathResource("user_prompt_template.av");

    public DefaultAgentService(IChatClientFactory chatClientFactory, ToolCallbackProvider toolCallbackProvider) {
        this.chatClientFactory = chatClientFactory;
        this.toolsService = new DefaultIToolsService(toolCallbackProvider);
        this.promptService = new AviatorPromptService();
    }

    @Override
    public ChatResponse callAgent(Agent agent, List<Event> eventsInScene) {
        LanguageModel llm = agent.getLanguageModel();
        String scenePrompt = agent.getScenePrompt();
        List<String> toolNames = agent.getToolNames();

        //获取可用的 tools
        List<ToolCallback> toolCallbacks = toolsService.findToolCallbacks(toolNames);

        Map<String, Object> variables = Map.of(
                "events", eventsInScene,
                "terminalId", eventsInScene.get(0).getTerminalId()
        );

        String userMessage = this.promptService.assembleUserMessage(templateResource, variables);

        ChatClient chatClient = this.chatClientFactory.getChatClient(llm);

        ChatOptions chatOptions = this.chatClientFactory.getDefaultChatOptions(llm);

        Prompt prompt = Prompt.builder()
                .chatOptions(chatOptions)
                .messages(new UserMessage(userMessage))
                .build()
                .augmentSystemMessage(scenePrompt);

                ChatResponse response = chatClient
                        .prompt(prompt)
                        .toolCallbacks(toolCallbacks)
                        .call()
                        .chatClientResponse()
                        .chatResponse();

        if (response != null) {
            log.info("测试输出 -- {}", response.getResult().getOutput().getText());
        }
        return response;
    }
}
