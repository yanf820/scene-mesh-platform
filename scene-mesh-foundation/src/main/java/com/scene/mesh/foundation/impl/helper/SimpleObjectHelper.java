/**
 *
 */
package com.scene.mesh.foundation.impl.helper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Date;
import java.util.Map;

/**
 * 对象转换工具
 */
public class SimpleObjectHelper {

    private static ObjectMapper objectMapper = null;

    static {

        objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    public static String map2json(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<String, Object> json2map(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, new TypeReference<>() {});
    }

    public static String objectData2json(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object json2ObjectData(String json, Class<?> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    public static <T> T json2GenericObject(String json, TypeReference<T> tr) {
        try {
            return objectMapper.readValue(json, tr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T str2Obj(String jsonStr, Class<T> tClass) {
        try {
            return objectMapper.readValue(jsonStr,tClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static String convertSimpleObject2String(Object val) {
        if (val instanceof String) {
            return (String) val;
        } else if (val instanceof Integer intVal) {
            return String.valueOf(intVal);
        } else if (val instanceof Long lngVal) {
            return String.valueOf(lngVal);
        } else if (val instanceof Float fltVal) {
            return String.valueOf(fltVal);
        } else if (val instanceof Date dateVal) {
            return String.valueOf(dateVal.getTime());
        }
        return val.toString();
    }

}
