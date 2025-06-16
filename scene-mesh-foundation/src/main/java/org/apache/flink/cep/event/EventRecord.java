package org.apache.flink.cep.event;

import lombok.Data;

/**
 * 
 */
@Data
public class EventRecord<T> {
    private T event;
    private String ruleId;
    private Integer ruleVersion;
}
