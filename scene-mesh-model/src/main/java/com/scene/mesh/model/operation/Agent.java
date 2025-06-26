package com.scene.mesh.model.operation;

import com.scene.mesh.model.action.IMetaAction;
import com.scene.mesh.model.llm.LanguageModel;
import lombok.Data;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.List;

@Data
public class Agent {

    private String id;

    private LanguageModel languageModel;

    private String scenePrompt;

    //mcp tools
    private List<String> toolNames;

    private List<IMetaAction> metaActions;

    //知识库 ID
    private List<String> knowledgeBaseIds;

}
