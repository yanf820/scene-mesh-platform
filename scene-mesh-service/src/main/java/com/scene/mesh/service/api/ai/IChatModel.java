package com.scene.mesh.service.api.ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;

import java.io.Serializable;
import java.util.List;

public interface IChatModel extends ChatModel, Serializable {

    /**
     * 获取模型ID
     *
     * @return 模型ID
     */
    String getModelId();

    /**
     * 模型名称
     * @return 模型名称
     */
    String getModelName();

    /**
     * 获取提供商名称
     *
     * @return 提供商名称
     */
    String getProvider();

}
