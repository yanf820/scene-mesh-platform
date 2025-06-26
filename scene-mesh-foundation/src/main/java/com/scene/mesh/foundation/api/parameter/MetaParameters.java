
package com.scene.mesh.foundation.api.parameter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scene.mesh.foundation.impl.helper.SimpleObjectHelper;
import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * json参数提取
 */
public class MetaParameters {

    private String parameters;
    @Getter
    private Map<String, Object> parameterMap;

    public MetaParameters(String parameters) throws JsonProcessingException {
        super();
        this.parameters = parameters;
        this.parameterMap = SimpleObjectHelper.json2map(parameters);
        if (this.parameterMap == null) {
            this.parameterMap = new HashMap<>();
        }
    }

    public String get(String name, String dftVal) {
        Object object = this.parameterMap.get(name);
        if (object == null) return dftVal;
        return object.toString();
    }

    public int getInt(String name, int dftVal) {
        Object object = this.parameterMap.get(name);
        if (object == null) return dftVal;
        return NumberUtils.toInt(object.toString(), dftVal);
    }

    public long getLong(String name, long dftVal) {
        Object object = this.parameterMap.get(name);
        if (object == null) return dftVal;
        return NumberUtils.toLong(object.toString(), dftVal);
    }

    public float getFloat(String name, float dftVal) {
        Object object = this.parameterMap.get(name);
        if (object == null) return dftVal;
        return NumberUtils.toFloat(object.toString(), dftVal);
    }

    public Double getDouble(String name, double dftVal) {
        Object object = this.parameterMap.get(name);
        if (object == null) return dftVal;
        return NumberUtils.toDouble(object.toString(), dftVal);
    }

    public List<String> getLines(String name) {
        String text = (String) this.parameterMap.get(name);
        List<String> ls = new ArrayList<String>();
        if (text != null) for (String line : text.split("[\\n\\r]+")) {
            ls.add(line.trim());
        }
        return ls;
    }

    public Map<String,Object> getMap(String name){
        Map<String,Object> map = (Map<String, Object>) this.parameterMap.get(name);
        return map;
    }

}
