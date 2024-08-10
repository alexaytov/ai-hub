package com.alexaytov.ai_hub.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfigurator {

    @Bean
    public ModelMapper mapper() {
        return new ModelMapper();
    }

    @Bean
    public RestTemplate template() {
        return new RestTemplate();
    }
}
