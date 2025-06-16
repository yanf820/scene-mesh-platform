package com.scene.mesh.service.impl.ai.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.ParameterizedTypeReference;

public class ActionStructuredOutputConverter<T> extends BeanOutputConverter<T> {
    public ActionStructuredOutputConverter(Class<T> clazz) {
        super(clazz);
    }

    public ActionStructuredOutputConverter(Class<T> clazz, ObjectMapper objectMapper) {
        super(clazz, objectMapper);
    }

    public ActionStructuredOutputConverter(ParameterizedTypeReference<T> typeRef) {
        super(typeRef);
    }

    public ActionStructuredOutputConverter(ParameterizedTypeReference<T> typeRef, ObjectMapper objectMapper) {
        super(typeRef, objectMapper);
    }
}
