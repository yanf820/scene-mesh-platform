package com.scene.mesh.foundation.impl.processor;

import com.scene.mesh.foundation.spec.processor.IProcessActivateContext;
import com.scene.mesh.foundation.spec.processor.IProcessInput;
import com.scene.mesh.foundation.spec.processor.IProcessOutput;
import com.scene.mesh.foundation.spec.processor.IProcessor;
import com.scene.mesh.foundation.impl.processor.standalone.ProcessTask;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 */
@Slf4j
public class BaseProcessor implements IProcessor {
    private static final long serialVersionUID = 8981180642607359756L;
    public static final String PROCESS_METHOD_PREFIX = "process";
    private Map<Class<?>, Method> processMethods;
    private Class<?>[] handleClzs;

    @Override
    public void activate(IProcessActivateContext activateContext) throws Exception {
        this.processMethods = new HashMap<>();

        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            String mName = method.getName();
            if (mName.startsWith(PROCESS_METHOD_PREFIX)) {
                Class<?>[] pTypes = method.getParameterTypes();
                if (pTypes != null && pTypes.length == 3) {
                    if (IProcessInput.class.isAssignableFrom(pTypes[1]) && IProcessOutput.class.isAssignableFrom
                            (pTypes[2])) {
                        this.processMethods.put(pTypes[0], method);
                    }
                }
            }
        }
        this.handleClzs = this.processMethods.keySet().toArray(new Class<?>[this.processMethods.size()]);

        log.info("Processor activated. ");
    }

    @Override
    public void deactivate() throws Exception {
        this.processMethods.clear();
        this.handleClzs = null;
        log.info("Processor deactivated. ");
    }

    @Override
    public void process(IProcessInput input, IProcessOutput output) throws Exception {
        Method method = null;
        if (input.hasInputObject()) {
            Object inputObject = input.getInputObject();
            boolean handled = this.process(inputObject, input, output);
            if (!handled) {
                method = this.processMethods.get(inputObject.getClass());
                if (method == null) {
                    for (Class<?> clz : handleClzs) {
                        if (clz.isInstance(inputObject)) {
                            method = this.processMethods.get(clz);
                            if (method != null) {
                                this.processMethods.put(clz, method);
                            }
                            break;
                        }
                    }
                }
                if (method != null) {
                    this.processByMethod(method, inputObject, input, output);
                } else {
                    throw new Exception("Can not found the process method for input object(" + inputObject + ") in " +
                            "Processor(" + this.getClass() + ")");
                }
            }
        } else {
            this.produce(input, output);
        }
    }

    protected Object getInputValue(Object obj) {
        if (obj instanceof ProcessTask task) {
            return task.getTaskObject();
        } else {
            return obj;
        }
    }

    protected void processByMethod(Method method, Object inputObject, IProcessInput input, IProcessOutput output)
            throws Exception {
        method.invoke(this, inputObject, input, output);
    }

    protected void produce(IProcessInput input, IProcessOutput output) throws Exception {
        //NOOP
    }

    protected boolean process(Object inputObject, IProcessInput input, IProcessOutput output) throws Exception {
        return false;
    }
}
