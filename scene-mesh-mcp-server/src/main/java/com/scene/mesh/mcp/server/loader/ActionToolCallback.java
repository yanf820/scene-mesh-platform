package com.scene.mesh.mcp.server.loader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scene.mesh.foundation.spec.message.IMessageProducer;
import com.scene.mesh.foundation.spec.message.MessageTopic;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.foundation.impl.helper.StringHelper;
import com.scene.mesh.model.action.Action;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;

import java.util.Map;

@Slf4j
public class ActionToolCallback implements ToolCallback {

    private final ToolCallback delegate;
    private final String toolName;
    private final IMessageProducer messageProducer;
    private final MessageTopic messageTopic;

    public ActionToolCallback(String toolName,ToolCallback delegate,IMessageProducer messageProducer) {
        this.delegate = delegate;
        this.toolName = toolName;
        this.messageProducer = messageProducer;
        this.messageTopic = new MessageTopic("outbound_actions");
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return this.delegate.getToolDefinition();
    }

    @Override
    public String call(String toolInput) {
        log.info("ActionToolCallback call toolInput:{}",toolInput);
        Action action = assembleAction(toolInput);

        try {
            messageProducer.send(this.messageTopic,action);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this.delegate.call(toolInput);
    }

    @Override
    public String call(String toolInput, ToolContext toolContext) {
        return this.call(toolInput);
    }

    private Action assembleAction(String toolInput) {
        Map<String,Object> inputMap;
        try {
            inputMap = SimpleObjectHelper.json2map(toolInput);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String terminalId = String.valueOf(inputMap.remove("terminalId"));
        if (terminalId == null) {
            throw new IllegalArgumentException(StringHelper.format("工具 - {0}被调用时,未发现传入的 terminalId",toolName));
        }

        //组装 Action
        Action action = new Action(toolName);
        action.setTerminalId(terminalId);
        action.setPayload(inputMap);

        return action;
    }
}
