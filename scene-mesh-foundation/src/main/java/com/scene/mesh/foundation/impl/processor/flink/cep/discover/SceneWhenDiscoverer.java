package com.scene.mesh.foundation.impl.processor.flink.cep.discover;

import com.scene.mesh.foundation.spec.component.IComponentProvider;
import org.apache.flink.cep.discover.PeriodicRuleDiscoverer;
import org.apache.flink.cep.event.Rule;

import java.util.*;

public class SceneWhenDiscoverer extends PeriodicRuleDiscoverer {

    private final String discoverComponent;
    private final IComponentProvider componentProvider;
    private IRuleDiscoverer ruleDiscoverer;

    public SceneWhenDiscoverer(Long intervalMillis, String discoverComponent, IComponentProvider componentProvider) {
        super(intervalMillis);
        this.discoverComponent = discoverComponent;
        this.componentProvider = componentProvider;
    }

    @Override
    public List<Rule> getLatestRules() throws Exception {
        if (ruleDiscoverer == null) {
            ruleDiscoverer = (IRuleDiscoverer) componentProvider.getComponent(discoverComponent);
        }
        return this.ruleDiscoverer.getRules();
    }
}
