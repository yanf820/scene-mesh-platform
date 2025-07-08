package com.scene.mesh.engin.processor.when.rule;

import com.scene.mesh.service.spec.scene.ISceneService;
import org.apache.flink.cep.discover.PeriodicRuleDiscoverer;
import org.apache.flink.cep.event.Rule;

import java.util.List;
import java.util.Map;

public class SceneRuleDiscoverer extends PeriodicRuleDiscoverer {

    private final ISceneService sceneService;

    public SceneRuleDiscoverer(Long intervalMillis, ISceneService sceneService) {
        super(intervalMillis);
        this.sceneService = sceneService;
    }

    @Override
    public List<Rule> getLatestRules() throws Exception {
//        Map<String,String> whenRules = this.sceneService.getAllSceneWhens();
        return List.of();
    }
}
