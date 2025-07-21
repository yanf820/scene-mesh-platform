package com.scene.mesh.foundation.impl.message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.scene.mesh.foundation.spec.message.IMessageSerializer;

import java.io.IOException;

/**
 */
public class JsonMessageSerializer implements IMessageSerializer {

    private final ObjectMapper objectMapper;

    public JsonMessageSerializer() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    @Override
    public byte[] serialize(Object message) throws IOException {
        return this.objectMapper.writeValueAsBytes(message);
    }

    @Override
    public <T> T deserialize(byte[] messageBytes, Class<T> messageType) throws IOException {
        return this.objectMapper.readValue(messageBytes, messageType);
    }
}
