package com.scene.mesh.foundation.impl.processor.flink.cep.discover;

import com.scene.mesh.foundation.spec.component.IComponentProvider;
import org.apache.flink.cep.discover.PeriodicRuleDiscoverer;
import org.apache.flink.cep.discover.PeriodicRuleDiscovererFactory;
import org.apache.flink.cep.event.Rule;

import javax.annotation.Nullable;
import java.util.List;

public class ScenePeriodicWhenDiscovererFactory extends PeriodicRuleDiscovererFactory {

    private final String discoverComponent;

    private final IComponentProvider componentProvider;

    public ScenePeriodicWhenDiscovererFactory(@Nullable List<Rule> initialRules, Long intervalMillis, String discoverComponent,IComponentProvider componentProvider) {
        super(initialRules, intervalMillis);
        this.discoverComponent = discoverComponent;
        this.componentProvider = componentProvider;
    }

    @Override
    public PeriodicRuleDiscoverer createRuleDiscoverer(ClassLoader userCodeClassLoader) throws Exception {
        return new SceneWhenDiscoverer(getIntervalMillis(),discoverComponent,componentProvider);
    }
}
