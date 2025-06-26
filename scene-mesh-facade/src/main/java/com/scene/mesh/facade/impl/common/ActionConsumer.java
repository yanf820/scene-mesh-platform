package com.scene.mesh.facade.impl.common;

import com.scene.mesh.facade.api.common.IMessageExchanger;
import com.scene.mesh.foundation.api.message.IMessageConsumer;
import com.scene.mesh.foundation.api.message.MessageTopic;
import com.scene.mesh.model.action.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ActionConsumer {

    @Autowired
    private IMessageConsumer messageConsumer;

    @Autowired
    private IMessageExchanger messageExchanger;

    private ExecutorService executorService;

    private boolean running = false;

    public ActionConsumer(IMessageConsumer messageConsumer) {
        this.executorService = Executors.newSingleThreadExecutor();
        this.messageConsumer = messageConsumer;
        this.startConsume();
    }

    public void startConsume(){
        running = true;
        MessageTopic topic = new MessageTopic("actions");
        this.executorService.submit(() -> {
            while (running) {
                List<Action> actions = messageConsumer.receive(topic, Action.class);
                if (actions == null || actions.isEmpty()) {
                    continue;
                }
                actions.forEach(action -> {
                    messageExchanger.handleOutboundAction(action);
                });
            }
        });
    }

}
