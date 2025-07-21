package com.scene.mesh.service.impl.scene;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.foundation.impl.processor.flink.cep.discover.IRuleDiscoverer;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.model.scene.WhenThen;
import com.scene.mesh.service.spec.cache.MutableCacheService;
import com.scene.mesh.service.spec.scene.ISceneService;
import org.apache.flink.cep.event.Rule;

import java.util.*;

public class DefaultSceneService implements ISceneService, IRuleDiscoverer {

    private final MutableCacheService mutableCacheService;

    public DefaultSceneService(MutableCacheService mutableCacheService) {
        this.mutableCacheService = mutableCacheService;
    }

    @Override
    public Scene getSceneById(String sceneId) {
        return mutableCacheService.getSceneById(sceneId);
    }

    public List<Scene> getAllScenes(){
        return mutableCacheService.getAllScenes();
    }

    @Override
    public List<Rule> getRules() throws Exception {
        List<Scene> scenes = this.getAllScenes();
        if (scenes == null || scenes.isEmpty()) {
            return List.of();
        }

        List<Rule> rules = new ArrayList<>();

        for (Scene scene : scenes) {
            List<WhenThen> whenThens = scene.getWhenThenList();
            if (whenThens == null || whenThens.isEmpty()) {
                continue;
            }
            for (WhenThen whenThen : whenThens){
                String when = whenThen.getWhen();
                when = extractCleanJson(when);
                WhenThen.Then then = whenThen.getThen();

                Rule rule = new Rule();
                rule.setId(UUID.randomUUID().toString());
                rule.setFunction("com.scene.mesh.engin.processor.when.SceneMatchedProcessor");
                Map<String, Object> params = new HashMap<>();
                params.put("thenId", then.getId());
                params.put("sceneId", scene.getId());
                rule.setParameters(SimpleObjectHelper.map2json(params));
                rule.setPattern(when);
                rule.setBindingKeys(new HashSet<>());
                rule.setVersion(1);
                rule.setLibs(new HashSet<>());

                rules.add(rule);
            }
        }
        return rules;
    }

    private String extractCleanJson(String escapedJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.readValue(escapedJson, String.class);
            Object jsonObject = mapper.readValue(jsonString, Object.class);

            // 重新序列化为干净的JSON
            return mapper.writeValueAsString(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return escapedJson;
        }
    }

//    @Override
//    public Pair<SceneRelationType,Scene> analyseScenesRelation(Scene currentScene, Scene matchedScene){
//        if (currentScene == null || matchedScene == null) {
//            throw new IllegalArgumentException("空场景无法进行关系分析. currentScene=" + SimpleObjectHelper.objectData2json(currentScene)
//                    + ", matchedScene=" + SimpleObjectHelper.objectData2json(matchedScene));
//        }
//
//        //相同
//        if (currentScene.getId().equals(matchedScene.getId())) {
//            return Pair.of(SceneRelationType.SAME, currentScene);
//        }
//
//        LinkedList<Scene> currentParentOfScene1 = this.cacheService.getParentScenesById(currentScene.getId());
//        LinkedList<Scene> matchedParentOfScene2 = this.cacheService.getParentScenesById(matchedScene.getId());
//
//        //直系关系，选择叶子场景
//        for (Scene ps1 : currentParentOfScene1) {
//            if (ps1.getId().equals(matchedScene.getId()))
//                return Pair.of(SceneRelationType.LINEAL,currentScene);
//        }
//
//        for (Scene ps2 : matchedParentOfScene2) {
//            if (ps2.getId().equals(currentScene.getId()))
//                return Pair.of(SceneRelationType.LINEAL,matchedScene);
//        }
//
//        //兄弟关系，选择优先级高的场景
//        if (currentParentOfScene1.getLast().getId().equals(matchedParentOfScene2.getLast().getId())){
//            if (currentScene.getPriority() > matchedScene.getPriority())
//                return Pair.of(SceneRelationType.SIBLING,currentScene);
//            else
//                return Pair.of(SceneRelationType.SIBLING,matchedScene);
//        }
//
//        //未知关系，选择匹配到的场景
//        return Pair.of(SceneRelationType.UNKNOWN,matchedScene);
//    }

}
