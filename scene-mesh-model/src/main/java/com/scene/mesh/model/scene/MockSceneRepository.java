package com.scene.mesh.model.scene;

import java.util.ArrayList;
import java.util.List;

public class MockSceneRepository {

   public List<Scene> getAllScenes(){
        List<Scene> scenes = new ArrayList<>();

        Scene scene1 = new Scene();
        scene1.setId("1");
        scene1.setName("Test Scene 1");

        Scene scene2 = new Scene();
        scene2.setId("2");
        scene2.setName("Test Scene 2");

        Scene scene3 = new Scene();
        scene3.setId("3");
        scene3.setName("Test Scene 3");

        scenes.add(scene1);
        scenes.add(scene2);
        scenes.add(scene3);

        return scenes;
    }
}
