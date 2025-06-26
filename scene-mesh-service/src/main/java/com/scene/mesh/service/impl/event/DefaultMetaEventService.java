package com.scene.mesh.service.impl.event;

import com.scene.mesh.foundation.api.parameter.MetaParameterDescriptor;
import com.scene.mesh.foundation.api.parameter.data.StringParameterDataType;
import com.scene.mesh.model.event.DefaultMetaEvent;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.service.api.event.IMetaEventService;

import java.util.ArrayList;
import java.util.List;

public class DefaultMetaEventService implements IMetaEventService {

    @Override
    public IMetaEvent getIMetaEvent(String metaEventId) {

        List<IMetaEvent> metaEvents = getAllMetaEvents();
        for (IMetaEvent metaEvent : metaEvents) {
            if (metaEventId.equals(metaEvent.getUuid()))
                return metaEvent;
        }
        return null;
    }

    private List<IMetaEvent> getAllMetaEvents(){
        List<IMetaEvent> metaEvents = new ArrayList<>();

        DefaultMetaEvent rfidEventModel = new DefaultMetaEvent("wakeup_rfid","rfid 唤醒事件","rfid 唤醒事件");
        rfidEventModel.addParameterDescriptor(new MetaParameterDescriptor("rfid","rfid","终端上传的 RFID",new StringParameterDataType(),true));

        DefaultMetaEvent wordEventModel = new DefaultMetaEvent("wakeup_word","关键词唤醒事件","关键词唤醒事件");
        wordEventModel.addParameterDescriptor(new MetaParameterDescriptor("word","唤醒词","终端唤醒的关键词",new StringParameterDataType(),true));

        metaEvents.add(rfidEventModel);
        metaEvents.add(wordEventModel);

        return metaEvents;
    }
}
