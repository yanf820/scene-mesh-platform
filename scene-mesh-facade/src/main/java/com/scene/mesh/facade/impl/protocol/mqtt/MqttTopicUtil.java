package com.scene.mesh.facade.impl.protocol.mqtt;

/**
 * mqtt topic tool
 */
public class MqttTopicUtil {

    public static final String TOPIC_PREFIX = "devices";

    public static String getErrorTopic(String clientId) {
        return TOPIC_PREFIX+"/"+clientId+"/error";
    }

    public static String getActionTopic(String clientId) {
        return TOPIC_PREFIX+"/"+clientId+"/action";
    }
}
