package com.scene.mesh.model.event;

import com.scene.mesh.foundation.api.parameter.MetaParameterDescriptor;
import com.scene.mesh.foundation.api.parameter.data.IntParameterDataType;
import com.scene.mesh.foundation.api.parameter.data.StringParameterDataType;
import com.scene.mesh.foundation.api.parameter.data.BooleParameterDataType;
import com.scene.mesh.foundation.api.parameter.data.DoubleParameterDataType;
import com.scene.mesh.foundation.api.parameter.data.TimeParameterDataType;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MockMetaEventRepository {

    /**
     * 为事件添加终端相关的基础参数描述符
     */
    private void addTerminalParameters(DefaultMetaEvent event) {
        // 添加终端ID参数
        event.addParameterDescriptor(new MetaParameterDescriptor(
                "terminalId",
                "终端ID",
                "事件发生的终端唯一标识",
                new StringParameterDataType(),
                true
        ));
    }

    public List<IMetaEvent> getAllMetaEvents(){
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
