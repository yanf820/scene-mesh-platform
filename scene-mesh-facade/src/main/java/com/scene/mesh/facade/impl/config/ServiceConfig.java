package com.scene.mesh.facade.impl.config;

import com.scene.mesh.model.terminal.TerminalRepository;
import com.scene.mesh.service.spec.product.IProductService;
import com.scene.mesh.service.spec.terminal.ITerminalService;
import com.scene.mesh.service.impl.product.DefaultProductService;
import com.scene.mesh.service.impl.terminal.DefaultTerminalService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public IProductService  productService(){
        return new DefaultProductService();
    }

    @Bean
    public ITerminalService terminalService(TerminalRepository terminalRepository){
        return new DefaultTerminalService(terminalRepository);
    }
}
