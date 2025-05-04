package com.scene.mesh.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class SmJsonSerializer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static <T> T str2Obj(String jsonStr, Class<T> tClass) {
        try {
            return objectMapper.readValue(jsonStr,tClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String obj2Str(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String,Object> str2Map(String jsonStr) {
        try {
            return objectMapper.readValue(jsonStr, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> obj2Map(Object obj) {
        return str2Map(obj2Str(obj));
    }
}
