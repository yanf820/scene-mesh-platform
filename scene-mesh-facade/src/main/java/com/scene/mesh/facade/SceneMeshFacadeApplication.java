package com.scene.mesh.facade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.scene.mesh")
@EnableJpaRepositories(basePackages = "com.scene.mesh.model")
@EntityScan("com.scene.mesh.model")
public class SceneMeshFacadeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SceneMeshFacadeApplication.class, args);
    }

}
