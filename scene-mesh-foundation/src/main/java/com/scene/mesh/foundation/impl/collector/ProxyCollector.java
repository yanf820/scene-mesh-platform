package com.scene.mesh.foundation.impl.collector;

import com.scene.mesh.foundation.api.collector.ICollector;

/**
 */
public class ProxyCollector implements ICollector {

    private ICollector delegateCollector;
    private int counter;

    public ProxyCollector(ICollector delegateCollector) {
        this.delegateCollector = delegateCollector;
        this.counter = 0;
    }

    @Override
    public void collect(Object object) {
        if(object!=null) this.counter ++;
        this.delegateCollector.collect(object);
    }

    public int getCollectedSize() {
        return this.counter;
    }
}
