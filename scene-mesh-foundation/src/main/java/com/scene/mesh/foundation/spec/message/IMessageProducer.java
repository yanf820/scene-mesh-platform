
package com.scene.mesh.foundation.spec.message;

/**
 * 消息生产者
 */
public interface IMessageProducer {

    void setSerializer(IMessageSerializer serializer);

    void send(MessageTopic topic, Object... messages) throws Exception;

}
