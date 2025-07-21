package com.scene.mesh.service.impl.ai.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ToolCallbackProviderManager {

    private final Map<String, ToolCallbackProvider> toolCallbackProviderMap;

    public ToolCallbackProviderManager() {
        toolCallbackProviderMap = new ConcurrentHashMap<>();
    }

    public void registerToolCallbackProvider(ToolCallbackProviderWithId toolCallbackProvider) {
        if (toolCallbackProvider == null) {
            return;
        }
        toolCallbackProviderMap.put(toolCallbackProvider.getServerName(), toolCallbackProvider);
    }

    public ToolCallbackProvider getToolCallbackProvider(String serverName) {
        return toolCallbackProviderMap.get(serverName);
    }

    public Collection<ToolCallbackProvider> getAllToolCallbackProvider() {
        return toolCallbackProviderMap.values();
    }
}
