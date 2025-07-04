package com.scene.mesh.model.event;

import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptor;
import com.scene.mesh.foundation.spec.parameter.MetaParameterDescriptorCollection;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class DefaultMetaEvent implements IMetaEvent {

    // 元事件 ID
    private String uuid;
    // 元事件名称
    private String name;
    // 元事件描述
    private String description;
    // 元事件参数
    private MetaParameterDescriptorCollection parameterCollection;

    public DefaultMetaEvent() {
    }

    public DefaultMetaEvent(String id, String name, String description) {
        this.uuid = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean validate(Map<String,Object> dataMap) {
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            MetaParameterDescriptor mpd = parameterCollection.findParameterDescriptorByName(entry.getKey());
            if (mpd == null) {
                log.error("无法匹配相应的事件元属性: json Key - {} ", entry.getKey());
                return false;
            }
        }
        return true;
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public MetaParameterDescriptorCollection getParameterCollection() {
        return this.parameterCollection;
    }

    public void addParameterDescriptor(MetaParameterDescriptor parameterDescriptor) {
        if (this.parameterCollection == null) {
            this.parameterCollection = new MetaParameterDescriptorCollection();
        }
        this.parameterCollection.addParameterDescriptor(parameterDescriptor);
    }
}
