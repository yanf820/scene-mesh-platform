package com.scene.mesh.service.spec.scene;

import com.scene.mesh.model.scene.Scene;

/**
 * 场景服务
 */
public interface ISceneService {

//    Pair<SceneRelationType,Scene> analyseScenesRelation(Scene currentScene, Scene matchedScene);

    Scene getSceneById(String sceneId);

    //
    void getAllSceneWhens();
}
