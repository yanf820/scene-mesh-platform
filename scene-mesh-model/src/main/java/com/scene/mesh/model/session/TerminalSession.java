package com.scene.mesh.model.session;

import com.scene.mesh.model.scene.Scene;
import lombok.Data;

import java.io.Serializable;

/**
 * 终端 session
 */
@Data
public class TerminalSession implements Serializable {
    //session ID
    private String sessionId;
    //产品 ID
    private String produceId;
    //终端 ID
    private String terminalId;
    //所处场景 ID
    private String locatedSceneId;

}
