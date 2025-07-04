package com.scene.mesh.foundation.impl.processor;

import com.scene.mesh.foundation.spec.message.IMessageProducer;
import com.scene.mesh.foundation.spec.message.MessageTopic;
import com.scene.mesh.foundation.spec.processor.IProcessInput;
import com.scene.mesh.foundation.spec.processor.IProcessOutput;

/**
 */
public class MessageSendProcessor extends BaseProcessor {

    private IMessageProducer messageProducer;
    private MessageTopic messageTopic;
    private String topicName;

    public IMessageProducer getMessageProducer() {
        return messageProducer;
    }

    public void setMessageProducer(IMessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
        this.messageTopic = new MessageTopic(topicName);
    }

    @Override
    protected boolean process(Object inputObject, IProcessInput input, IProcessOutput output) throws Exception {
        Object[] objs = this.handleObjectToMessages(inputObject);
        this.messageProducer.send(this.messageTopic, objs);
        return true;
    }

    protected Object[] handleObjectToMessages(Object inputObject) {
        return new Object[]{inputObject};
    }
}
