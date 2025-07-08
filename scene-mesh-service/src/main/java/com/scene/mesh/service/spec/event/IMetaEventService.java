package com.scene.mesh.service.spec.event;

import com.scene.mesh.model.event.IMetaEvent;
import com.scene.mesh.model.product.OriginalProduct;

import java.util.List;

public interface IMetaEventService {

    IMetaEvent getIMetaEvent(String metaEventId);

    List<IMetaEvent> getAllMetaEvents();

}
