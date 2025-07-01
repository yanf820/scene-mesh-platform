package com.scene.mesh.manager.config;

import com.scene.mesh.model.terminal.TerminalRepository;
import com.scene.mesh.service.impl.terminal.DefaultTerminalService;
import com.scene.mesh.service.spec.terminal.ITerminalService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public ITerminalService terminalService(TerminalRepository terminalRepository){
        return new DefaultTerminalService(terminalRepository);
    }

}
