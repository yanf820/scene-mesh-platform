package com.scene.mesh.service.api.ai;

import org.springframework.ai.tool.ToolCallback;

import java.util.List;

public interface IToolsService {

    //根据 tool name 获取 toolcallback
    List<ToolCallback> findToolCallbacks(List<String> toolNames);
}
