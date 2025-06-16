package com.scene.mesh.engin.processor.process.operator;

import com.scene.mesh.engin.model.OperationRequest;
import com.scene.mesh.engin.model.OperationResponse;
import com.scene.mesh.model.action.IMetaAction;
import com.scene.mesh.model.event.Event;
import com.scene.mesh.model.event.OutputEventType;
import com.scene.mesh.model.operation.Agent;
import com.scene.mesh.model.operation.Operation;
import com.scene.mesh.model.session.TerminalSession;
import com.scene.mesh.service.api.ai.IAgentService;
import org.springframework.ai.chat.model.ChatResponse;

import java.util.List;

public class AgentOperator implements IOperator{

    private IAgentService agentService;

    @Override
    public Operation.OperationType getOperationType() {
        return Operation.OperationType.AGENT;
    }

    @Override
    public void process(OperationRequest operationRequest, OperationResponse operationResponse, TerminalSession session) {
        Operation operation = operationRequest.getOperation();
        Agent agent = operation.getAgent();
        List<Event> eventsInScene = operationRequest.getEventsInScene();

        ChatResponse response = agentService.callAgent(agent,eventsInScene);
        String assistantMessage = response.getResult().getOutput().getText();

        Event event = new Event(OutputEventType.ASSISTANT.getName());
        event.setProductId(operationRequest.getProductId());
        event.setTerminalId(operationRequest.getTerminalId());
        event.addPayloadEntry("assistantMessage", assistantMessage);
        operationResponse.addEvent(event);
    }
}
