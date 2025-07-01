package com.scene.mesh.service.spec.ai;

import com.scene.mesh.model.event.Event;
import com.scene.mesh.model.operation.Agent;
import org.springframework.ai.chat.model.ChatResponse;

import java.util.List;

/**
 * Agent 服务
 */
public interface IAgentService {

    ChatResponse callAgent(Agent agent, List<Event> eventsInScene);

}
