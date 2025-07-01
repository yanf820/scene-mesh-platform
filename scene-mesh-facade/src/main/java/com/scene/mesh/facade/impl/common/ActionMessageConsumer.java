package com.scene.mesh.facade.impl.common;

import com.scene.mesh.facade.spec.common.IMessageExchanger;
import com.scene.mesh.foundation.spec.message.IMessageConsumer;
import com.scene.mesh.foundation.spec.message.MessageTopic;
import com.scene.mesh.model.action.Action;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ActionMessageConsumer {

    private final IMessageConsumer messageConsumer;

    private final IMessageExchanger messageExchanger;

    private final ExecutorService executorService;

    private final MessageTopic outboundActionTopic;

    private boolean running = false;

    public ActionMessageConsumer(IMessageConsumer messageConsumer, IMessageExchanger messageExchanger, MessageTopic outboundActionTopic) {
        this.executorService = Executors.newSingleThreadExecutor();
        this.messageConsumer = messageConsumer;
        this.messageExchanger = messageExchanger;
        this.outboundActionTopic = outboundActionTopic;
        this.startConsume();
    }

    public void startConsume(){
        running = true;

        this.executorService.submit(() -> {
            while (running) {
                List<Action> actions = null;
                try {
                    actions = messageConsumer.receive(outboundActionTopic, Action.class);
                } catch (Exception e) {
                    stopConsume();
                }
                if (actions == null || actions.isEmpty()) {
                    continue;
                }
                actions.forEach(action -> {
                    messageExchanger.handleOutboundAction(action);
                });
            }
        });
    }

    public void stopConsume(){
        running = false;
    }

}
