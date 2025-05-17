
package com.scene.mesh.foundation.api.message;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息 topic
 */
@Setter
@Getter
public class MessageTopic {

    private String topicName;

    public MessageTopic(String topicName) {
        this.topicName = topicName;
    }
}
