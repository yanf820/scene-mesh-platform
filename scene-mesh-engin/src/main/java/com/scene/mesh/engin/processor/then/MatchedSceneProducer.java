package com.scene.mesh.engin.processor.then;

import com.scene.mesh.engin.model.SceneMatchedResult;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.foundation.impl.processor.MessageReceiveProducer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MatchedSceneProducer extends MessageReceiveProducer<SceneMatchedResult> {

    @Override
    protected List<SceneMatchedResult> handleMessageList(List<SceneMatchedResult> list) {
        log.info("匹配规则读取: {}", SimpleObjectHelper.objectData2json(list));
        return super.handleMessageList(list);
    }

}
