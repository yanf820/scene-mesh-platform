package com.scene.mesh.engin.generator;

import org.apache.flink.cep.configuration.ObjectConfiguration;
import org.apache.flink.cep.dynamic.condition.AviatorCondition;
import org.apache.flink.cep.dynamic.impl.json.util.CepJsonUtils;
import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.types.Row;

/**
 * 规则生成
 */
public class PatternRuleGenerator {
    public static void main(String[] args) throws Exception {
        Pattern<Row, Row> pattern = Pattern.<Row>begin("start", AfterMatchSkipStrategy.skipPastLastEvent())
                .where(new AviatorCondition<>("metaEventId == 'shark' && 'sharkVal' == 5",null))
//                .where()
                .or(new AviatorCondition<>("metaEventId == 'voice' && string.contains(text, '啊')",null))
//                .followedBy()
                .within(Time.seconds(10));

        System.out.println(CepJsonUtils.convertPatternToJSONString(pattern));
    }
}
