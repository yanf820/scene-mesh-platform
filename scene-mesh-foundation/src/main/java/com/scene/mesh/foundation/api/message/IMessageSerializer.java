
package com.scene.mesh.foundation.api.message;

import java.io.IOException;

/**
 * 消息序列化器
 */
public interface IMessageSerializer {

    public byte[] serialize(Object message) throws IOException;

    public <T> T deserialize(byte[] messageBytes, Class<T> messageType) throws IOException;

}
