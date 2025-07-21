package com.scene.mesh.model.scene;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Scene implements Serializable {

    private String id;
    private String productId;
    private String name;
    private String description;
    private boolean enable;
    private List<WhenThen> whenThenList;
    private String flowDataPublishTime;
    private String prompt;

}
