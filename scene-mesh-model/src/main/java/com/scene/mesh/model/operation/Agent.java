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

    //场景提示词模板
    @Deprecated //TODO 删除
    private PromptTemplate scenePromptTemplate;

    //用户提示词模板
    @Deprecated //TODO 删除
    private PromptTemplate userPromptTemplate;

    private String scenePrompt;

    //mcp tools
    private List<String> toolNames;

    private List<IMetaAction> metaActions;

    //知识库 ID
    private List<String> knowledgeBaseIds;

}
