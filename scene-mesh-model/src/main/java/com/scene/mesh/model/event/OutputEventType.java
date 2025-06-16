package com.scene.mesh.model.event;

/**
 * 输出事件的类型
 */
public enum OutputEventType {

    ASSISTANT("assistant"), // 助手事件
    ERROR("error");

    private final String name;

    OutputEventType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
