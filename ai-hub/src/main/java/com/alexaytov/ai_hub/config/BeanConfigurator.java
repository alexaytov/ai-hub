package com.alexaytov.ai_hub.config;

import com.alexaytov.ai_hub.model.entities.DataSource;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfigurator {

  @Bean
  public ModelMapper mapper() {
    ModelMapper mapper = new ModelMapper();

    mapper.addConverter(
        new Converter<DataSource, Long>() {
          @Override
          public Long convert(MappingContext<DataSource, Long> mappingContext) {
            return mappingContext.getSource() != null ? mappingContext.getSource().getId() : null;
          }
        });

    return mapper;
  }

  @Bean
  public RestTemplate template() {
    return new RestTemplate();
  }
}
