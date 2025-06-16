package com.scene.mesh.model.llm;

import lombok.Data;

import java.util.List;

@Data
public class LanguageModel {
    private String id;
    private LanguageModelProvider provider;
    private String name;
    private String description;
    private List<LanguageModelType> feature; // type: 'array' of enum -> List<String>
}
