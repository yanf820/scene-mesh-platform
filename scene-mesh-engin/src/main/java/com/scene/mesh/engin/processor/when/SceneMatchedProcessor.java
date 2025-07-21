package com.scene.mesh.engin.processor.when;

import com.scene.mesh.engin.model.SceneMatchedResult;
import com.scene.mesh.model.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.cep.functions.AbstractPatternProcessFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 对匹配到的数据进行处理
 *
 * 
 */
@Slf4j
public class SceneMatchedProcessor extends AbstractPatternProcessFunction<Event, SceneMatchedResult> {

    private String thenId;

    private String sceneId;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        if (parameters != null && parameters.containsKey("thenId")) {
            this.thenId = parameters.getString("thenId", "unknown");
        } else {
            this.thenId = "unknown";
        }

        if (parameters != null && parameters.containsKey("sceneId")) {
            this.sceneId = parameters.getString("sceneId", "unknown");
        } else {
            this.sceneId = "unknown";
        }
    }

    @Override
    public void processMatch(Map<String, List<Event>> match, Context ctx, Collector<SceneMatchedResult> out) throws Exception {
        log.info("匹配规则成功 - scene id: {} , then id：{}", sceneId, thenId);

        List<Event> events = new ArrayList<>();
        for (Map.Entry<String, List<Event>> entry : match.entrySet()) {
            events.addAll(entry.getValue());
        }

        SceneMatchedResult result = new SceneMatchedResult();
        result.setSceneId(sceneId);
        result.setThenId(thenId);
        result.setTerminalId(events.get(0).getTerminalId());
        result.setMatchedEvents(events);
        result.setMatchedTime(System.currentTimeMillis());

        out.collect(result);

    }
}