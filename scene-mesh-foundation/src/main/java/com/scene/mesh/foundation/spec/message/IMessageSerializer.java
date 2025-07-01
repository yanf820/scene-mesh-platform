
package com.scene.mesh.foundation.spec.message;

import java.io.IOException;

/**
 * 消息序列化器
 */
public interface IMessageSerializer {

    byte[] serialize(Object message) throws IOException;

    <T> T deserialize(byte[] messageBytes, Class<T> messageType) throws IOException;

}
