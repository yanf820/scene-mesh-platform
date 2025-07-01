/**  
 * Copyright © 2016 ETERNITY. All rights reserved.
 * @Title: IProcessor.java
 * @Prject: wisdomplanet-foundation
 * @Package: org.wisdomplanet.foundation.processor
 * @Description: TODO
 * @author: A.Z  
 * @date: 2016-12-05
 * @version: V1.0  
 */
package com.scene.mesh.foundation.spec.processor;

import java.io.Serializable;

public interface IProcessor extends Serializable {

    void activate(IProcessActivateContext activateContext) throws Exception;

    void deactivate() throws Exception;

    void process(IProcessInput input, IProcessOutput output) throws Exception;

}