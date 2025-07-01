package com.scene.mesh.model.scene;

import com.scene.mesh.model.operation.Operation;
import lombok.Data;

import java.io.Serializable;

@Data
public class Scene implements Serializable {

    private String id;
    private String name;
    private String description;
    private boolean enable;

    private String rules; // type: 'json' -> String (holds JSON content)
    //场景优先级
    private int priority;
    //场景操作
    private Operation operation;

}
