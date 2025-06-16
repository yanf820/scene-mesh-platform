package com.scene.mesh.service.impl.ai.template;

import com.googlecode.aviator.AviatorEvaluator;
import org.springframework.ai.template.TemplateRenderer;

import java.util.Map;

public class AviatorTemplateRenderer implements TemplateRenderer {
    @Override
    public String apply(String template, Map<String, Object> variables) {
        Object result = AviatorEvaluator.execute(template, variables, true);
        return result.toString();
    }
}
