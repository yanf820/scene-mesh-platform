/**  
 * Copyright © 2016 ETERNITY. All rights reserved.
 * @Title: IProcessInput.java
 * @Prject: wisdomplanet-foundation
 * @Package: org.wisdomplanet.foundation.processor
 * @Description: TODO
 * @author: A.Z  
 * @date: 2016-12-05
 * @version: V1.0  
 */
package com.scene.mesh.foundation.api.processor;

import java.io.Serializable;


public interface IProcessInput extends Serializable {

    Object getInputObject();

    boolean hasInputObject();

}
