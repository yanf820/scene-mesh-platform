package com.scene.mesh.service.spec.action;

import com.scene.mesh.model.action.IMetaAction;
import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.model.product.OriginalProduct;

import java.util.List;

public interface IMetaActionService {

    IMetaAction getIMetaAction(String metaActionId);

    List<IMetaAction> getAllMetaActions();

}
