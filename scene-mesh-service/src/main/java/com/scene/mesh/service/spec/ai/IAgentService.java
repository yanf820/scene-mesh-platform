package com.scene.mesh.service.spec.ai;

import com.scene.mesh.model.event.Event;
import com.scene.mesh.model.scene.WhenThen;
import org.springframework.ai.chat.model.ChatResponse;

import java.util.List;

/**
 * Agent 服务
 */
public interface IAgentService {

    boolean callAgent(WhenThen.Then agent, List<Event> inputEvents);

}
