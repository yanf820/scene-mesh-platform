package com.scene.mesh.service.impl.ai;

import com.scene.mesh.model.action.IMetaAction;
import com.scene.mesh.model.event.Event;
import com.scene.mesh.model.llm.LanguageModel;
import com.scene.mesh.model.operation.Agent;
import com.scene.mesh.service.api.ai.IAgentService;
import com.scene.mesh.service.api.ai.IChatClientFactory;
import com.scene.mesh.service.api.ai.IPromptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DefaultAgentService implements IAgentService {

    private IChatClientFactory chatClientFactory;

    private IPromptService promptService;

    private Resource templateResource = new ClassPathResource("user_prompt_template.av");

    public DefaultAgentService(IChatClientFactory chatClientFactory) {
        this.chatClientFactory = chatClientFactory;
        this.promptService = new AviatorPromptService();
    }

    @Override
    public ChatResponse callAgent(Agent agent, List<Event> eventsInScene) {
        LanguageModel llm = agent.getLanguageModel();
        String scenePrompt = agent.getScenePrompt();
        List<String> toolNames = agent.getToolNames();
        List<IMetaAction> metaActions = agent.getMetaActions();

        Map<String, Object> variables = Map.of(
                "events", eventsInScene,
                "metaActions", metaActions
        );

        String userMessage = this.promptService.assembleUserMessage(templateResource,variables);

        ChatClient chatClient = this.chatClientFactory.getChatClient(llm, toolNames);
        ChatResponse response = chatClient.prompt().user(userMessage).system(scenePrompt).call().chatClientResponse().chatResponse();
        log.info("测试输出 -- {}",response.getResult().getOutput().getText());
        return null;
    }
}
