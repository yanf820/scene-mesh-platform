package com.scene.mesh.engin.processor.match;

import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.foundation.impl.processor.MessageReceiveProducer;
import com.scene.mesh.model.event.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class EventProducer extends MessageReceiveProducer<Event> {

    @Override
    protected List<Event> handleMessageList(List<Event> list) {
        log.info("EventProducer handleMessageList: {}", SimpleObjectHelper.objectData2json(list));
        return super.handleMessageList(list);
    }
}
