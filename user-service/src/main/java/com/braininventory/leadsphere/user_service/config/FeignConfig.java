package com.braininventory.leadsphere.user_service.config;


import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                // Grab the "Authorization" header from the current incoming request
                String authToken = attributes.getRequest().getHeader("Authorization");
                if (authToken != null) {
                    // Forward it to the Feign call (Lead Service)
                    requestTemplate.header("Authorization", authToken);
                }
            }
        };
    }
}