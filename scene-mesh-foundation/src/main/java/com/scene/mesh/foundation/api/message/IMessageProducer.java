
package com.scene.mesh.foundation.api.message;

/**
 * 消息生产者
 */
public interface IMessageProducer {

    public void setSerializer(IMessageSerializer serializer);

    public void send(MessageTopic topic, Object... messages) throws Exception;

}
