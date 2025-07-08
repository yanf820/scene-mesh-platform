package com.scene.mesh.engin.processor.cache;

import com.scene.mesh.foundation.impl.processor.CrontabTrigger;

import java.time.Duration;

public class CacheTrigger extends CrontabTrigger<String> {

    public CacheTrigger(Duration duration) {
        super(duration);
    }

    @Override
    protected String triggerOutput() {
        return "refresh cache";
    }
}
