package com.scene.mesh.foundation.impl.processor;

import com.scene.mesh.foundation.spec.processor.IProcessInput;
import com.scene.mesh.foundation.spec.processor.IProcessOutput;

import java.time.Duration;

public class CrontabTrigger<T> extends BaseProcessor {

    private final Duration duration;

    public CrontabTrigger(Duration duration) {
        this.duration = duration;
    }

    @Override
    protected void produce(IProcessInput input, IProcessOutput output) throws Exception {
        T t = triggerOutput();
        if (t != null) {
            output.getCollector().collect(t);
        }
        Thread.sleep(duration.toMillis());
    }

    protected T triggerOutput() {
        return null;
    }
}
