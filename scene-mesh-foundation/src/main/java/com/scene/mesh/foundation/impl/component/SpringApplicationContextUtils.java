package com.scene.mesh.foundation.impl.component;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring XML AppContext加载工具
 */
public class SpringApplicationContextUtils {

    @lombok.Setter
    private static ApplicationContext applicationContext;
    @lombok.Setter
    @lombok.Getter
    private static String contextId;

    public static synchronized ApplicationContext getApplicationContext() {
        if (applicationContext != null) {
            return applicationContext;
        } else {
            //synchronized (lockObj) {
            applicationContext = new ClassPathXmlApplicationContext(contextId == null ? "worker.xml" : contextId);
            return applicationContext;
            //}
        }
    }

}
