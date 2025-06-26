package com.scene.mesh.service.api.scene;

import com.scene.mesh.model.scene.Scene;
import org.apache.commons.lang3.tuple.Pair;

/**
 * 场景服务
 */
public interface ISceneService {

//    Pair<SceneRelationType,Scene> analyseScenesRelation(Scene currentScene, Scene matchedScene);

    Scene getSceneById(String sceneId);
}
