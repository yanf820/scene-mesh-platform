package com.scene.mesh.mcp.server.provider;

import com.scene.mesh.foundation.api.message.IMessageProducer;
import com.scene.mesh.foundation.api.parameter.IMetaParameterized;
import com.scene.mesh.foundation.api.parameter.MetaParameterDescriptor;
import com.scene.mesh.foundation.api.parameter.MetaParameterDescriptorCollection;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.mcp.server.loader.ActionToolCallback;
import com.scene.mesh.mcp.server.loader.IToolDrawingLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态工具提供者 - 基于 ActionLoader 元数据使用 MethodToolCallback 动态生成工具
 * 实现了 ToolCallbackProvider 会被自动装配
 */
@Component
@Slf4j
public class DynamicToolProvider implements ToolCallbackProvider {

    @Autowired
    private IToolDrawingLoader toolDrawingLoader;

    @Autowired
    private IMessageProducer messageProducer;

    private final Map<String, ToolCallback> toolCallbackCache = new ConcurrentHashMap<>();
    
    /**
     * 获取所有动态生成的工具回调
     */
    @Override
    public ToolCallback[] getToolCallbacks() {
        if (toolCallbackCache.isEmpty()) {
            initializeToolCallbacks();
        }
        return toolCallbackCache.values().toArray(new ToolCallback[0]);
    }

    /**
     * 初始化工具回调
     */
    private void initializeToolCallbacks() {
        List<IMetaParameterized> toolDrawings = toolDrawingLoader.loadToolDrawings();
        
        for (IMetaParameterized toolDrawing : toolDrawings) {
            try {
                ToolCallback toolCallback = createMethodToolCallback(toolDrawing);
                toolCallbackCache.put(toolDrawing.getUuid(), toolCallback);
                log.info("动态创建 MethodToolCallback 工具: {} - {}", toolDrawing.getUuid(), toolDrawing.getName());
            } catch (Exception e) {
                log.error("创建动态工具失败: {} - {}", toolDrawing.getUuid(), toolDrawing.getName(), e);
            }
        }
    }

    /**
     * 根据 IMetaAction 创建 MethodToolCallback
     */
    private ToolCallback createMethodToolCallback(IMetaParameterized toolDrawing) throws Exception {
        // 构建工具定义
        ToolDefinition toolDefinition = createToolDefinition(toolDrawing);
        
        // 填充默认
        Method method = getNullMethod();
        
        // 使用 Builder 模式创建 MethodToolCallback
        MethodToolCallback toolCallback = MethodToolCallback.builder()
                .toolDefinition(toolDefinition)
                .toolMethod(method)
                .toolObject(this)  // 当前对象作为工具对象
                .build();

        return new ActionToolCallback(toolDefinition.name(), toolCallback, messageProducer);
    }

    /**
     * 创建工具定义
     */
    private ToolDefinition createToolDefinition(IMetaParameterized toolDrawing) {
        Map<String, Object> parameterSchema = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();

        MetaParameterDescriptorCollection paramCollection = toolDrawing.getParameterCollection();
        if (paramCollection != null && paramCollection.getParameterDescriptors() != null) {
            for (MetaParameterDescriptor param : paramCollection.getParameterDescriptors()) {
                Map<String, Object> paramDef = new HashMap<>();
                paramDef.put("type", "string"); // 简化处理，都当作字符串
                paramDef.put("description", param.getDescription());
                
                properties.put(param.getName(), paramDef);
                
                if (param.isRequired()) {
                    required.add(param.getName());
                }
            }
            // 自定义添加结构数据
            Map<String, Object> productIdDef = new HashMap<>();
            productIdDef.put("type", "string");
            productIdDef.put("description", "产品 ID");
            properties.put("productId", productIdDef);

            Map<String, Object> terminalIdDef = new HashMap<>();
            terminalIdDef.put("type", "string");
            terminalIdDef.put("description", "终端 ID");
            properties.put("terminalId", terminalIdDef);
        }

        parameterSchema.put("type", "object");
        parameterSchema.put("properties", properties);
        if (!required.isEmpty()) {
            parameterSchema.put("required", required);
        }

        return DefaultToolDefinition.builder()
                .name(toolDrawing.getUuid())
                .description(toolDrawing.getDescription())
                .inputSchema(Objects.requireNonNull(SimpleObjectHelper.map2json(parameterSchema)))
                .build();
    }

    private Method getNullMethod() throws NoSuchMethodException {
        return this.getClass().getMethod("nullMethod");
    }

    public void nullMethod() {
    }

    /**
     * 清除缓存，重新加载工具
     */
    public void refresh() {
        toolCallbackCache.clear();
        initializeToolCallbacks();
        log.info("🔄 动态工具已刷新，共加载 {} 个工具", toolCallbackCache.size());
    }
} 