package com.scene.mesh.model.product;

import com.scene.mesh.model.action.IMetaAction;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.model.scene.Scene;
import lombok.Data;

import java.util.List;

/**
 * 产品模型
 */
@Data
public class Product {
    private String id;
    private String name;
    private String description;
    private String image; // type: 'binary' -> String (URL or path)
    private String category; // type: 'enum' -> String
    private ProductSetting settings; // type: 'one_to_one'
    private List<IMetaEvent> metaEvents; // type: 'one_to_many'
    private List<IMetaAction> metaActions; // type: 'one_to_many'
    private Scene rootScene; // type: 'one_to_one'

}
