
package com.scene.mesh.foundation.api.message;

import java.util.List;

/**
 * 消息消费者
 */
public interface IMessageConsumer {

    void setSerializer(IMessageSerializer serializer);

    <T> List<T> receive(MessageTopic topic, Class<T> messageType);

}
