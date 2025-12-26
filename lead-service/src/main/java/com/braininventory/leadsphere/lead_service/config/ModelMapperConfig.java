package com.braininventory.leadsphere.lead_service.config;


import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setSkipNullEnabled(true) // Crucial for PATCH/Partial updates
                .setMatchingStrategy(MatchingStrategies.STRICT) // Prevents ambiguous mapping
                .setFieldMatchingEnabled(true);

        return modelMapper;
    }
}