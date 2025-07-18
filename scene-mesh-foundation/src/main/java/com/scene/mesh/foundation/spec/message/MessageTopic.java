
package com.scene.mesh.foundation.spec.message;

import lombok.Getter;

/**
 * 消息 topic
 */
public class MessageTopic {

    @Getter
    private String topicName;

    public MessageTopic(String topicName) {
        this.topicName = topicName;
    }
}
