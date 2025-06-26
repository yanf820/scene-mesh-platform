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

    // 匹配的规则 ID
    private String ruleId;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        // 从Configuration中获取ruleId
        if (parameters != null && parameters.containsKey("ruleId")) {
            this.ruleId = parameters.getString("ruleId", "unknown");
        } else {
            this.ruleId = "unknown";
        }
    }

    @Override
    public void processMatch(Map<String, List<Event>> match, Context ctx, Collector<SceneMatchedResult> out) throws Exception {
        log.info("匹配规则成功 - 规则 ID：{}", ruleId);

        List<Event> events = new ArrayList<>();
        for (Map.Entry<String, List<Event>> entry : match.entrySet()) {
            events.addAll(entry.getValue());
        }

        SceneMatchedResult result = new SceneMatchedResult();
        result.setSceneId("1");//TODO 获取规则关联的场景 ID
        result.setRuleId(ruleId);
        result.setTerminalId(events.get(0).getTerminalId());
        result.setMatchedEvents(events);
        result.setMatchedTime(System.currentTimeMillis());

        out.collect(result);

    }
}