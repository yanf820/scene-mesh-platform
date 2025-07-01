package com.scene.mesh.service.impl.ai;

import com.scene.mesh.service.spec.ai.IToolsService;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

import java.util.ArrayList;
import java.util.List;

public class DefaultIToolsService implements IToolsService {

    private ToolCallbackProvider toolCallbackProvider;

    public DefaultIToolsService(ToolCallbackProvider toolCallbackProvider) {
        this.toolCallbackProvider = toolCallbackProvider;
    }

    @Override
    public List<ToolCallback> findToolCallbacks(List<String> toolNames) {
        if (toolNames == null || toolNames.isEmpty()) {
            return null;
        }

        List<ToolCallback> discoveredToolCallbacks = new ArrayList<>();

        ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
        for (ToolCallback toolCallback : toolCallbacks) {
            for (String toolName : toolNames) {
                if (toolCallback.getToolDefinition().name().contains(toolName))
                    discoveredToolCallbacks.add(toolCallback);
            }
        }

        return discoveredToolCallbacks;
    }
}
