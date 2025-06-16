package com.scene.mesh.service.impl.scene;

import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import com.scene.mesh.model.scene.Scene;
import com.scene.mesh.service.api.cache.MutableCacheService;
import com.scene.mesh.service.api.scene.ISceneService;
import com.scene.mesh.service.api.scene.SceneRelationType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;

public class SceneService implements ISceneService {

    private MutableCacheService cacheService;

    public SceneService(MutableCacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public Pair<SceneRelationType,Scene> analyseScenesRelation(Scene currentScene, Scene matchedScene){
        if (currentScene == null || matchedScene == null) {
            throw new IllegalArgumentException("空场景无法进行关系分析. currentScene=" + SimpleObjectHelper.objectData2json(currentScene)
                    + ", matchedScene=" + SimpleObjectHelper.objectData2json(matchedScene));
        }

        //相同
        if (currentScene.getId().equals(matchedScene.getId())) {
            return Pair.of(SceneRelationType.SAME, currentScene);
        }

        LinkedList<Scene> currentParentOfScene1 = this.cacheService.getParentScenesById(currentScene.getId());
        LinkedList<Scene> matchedParentOfScene2 = this.cacheService.getParentScenesById(matchedScene.getId());

        //直系关系，选择叶子场景
        for (Scene ps1 : currentParentOfScene1) {
            if (ps1.getId().equals(matchedScene.getId()))
                return Pair.of(SceneRelationType.LINEAL,currentScene);
        }

        for (Scene ps2 : matchedParentOfScene2) {
            if (ps2.getId().equals(currentScene.getId()))
                return Pair.of(SceneRelationType.LINEAL,matchedScene);
        }

        //兄弟关系，选择优先级高的场景
        if (currentParentOfScene1.getLast().getId().equals(matchedParentOfScene2.getLast().getId())){
            if (currentScene.getPriority() > matchedScene.getPriority())
                return Pair.of(SceneRelationType.SIBLING,currentScene);
            else
                return Pair.of(SceneRelationType.SIBLING,matchedScene);
        }

        //未知关系，选择匹配到的场景
        return Pair.of(SceneRelationType.UNKNOWN,matchedScene);
    }

}
