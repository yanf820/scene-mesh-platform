package com.scene.mesh.mcp.server.loader;

import com.scene.mesh.foundation.spec.parameter.IMetaParameterized;

import java.util.List;

public interface IToolDrawingLoader {

    // 加载工具的设计图
    List<IMetaParameterized> loadToolDrawings();

}
