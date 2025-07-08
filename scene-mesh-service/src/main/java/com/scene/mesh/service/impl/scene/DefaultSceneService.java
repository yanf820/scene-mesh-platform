package com.scene.mesh.service.impl.scene;

import com.scene.mesh.model.llm.LanguageModel;
import com.scene.mesh.model.llm.LanguageModelProvider;
import com.scene.mesh.model.operation.Agent;
import com.scene.mesh.model.operation.Operation;
import com.scene.mesh.model.product.OriginalProduct;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.model.scene.WhenThen;
import com.scene.mesh.service.spec.cache.MutableCacheService;
import com.scene.mesh.service.spec.scene.ISceneService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultSceneService implements ISceneService {

    private final MutableCacheService mutableCacheService;

    public DefaultSceneService(MutableCacheService mutableCacheService) {
        this.mutableCacheService = mutableCacheService;
    }

    @Override
    public Scene getSceneById(String sceneId) { //TODO 补充
        return mutableCacheService.getSceneById(sceneId);
    }

    public List<Scene> getAllScenes(){
        return mutableCacheService.getAllScenes();
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
