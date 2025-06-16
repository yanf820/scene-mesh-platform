package com.scene.mesh.model.action;

import com.scene.mesh.model.event.Event;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.Map;

/**
 * Action
 */
@Data
public class Action {
    //Action ID
    @Setter(AccessLevel.NONE)
    private String id;
    //Action元模型 ID
    @Setter(AccessLevel.NONE)
    private String metaActionId;
    //产品 ID
    private String productId;
    //终端 ID
    private String terminalId;
    //payload
    private Map<String, Object> payload;
}
