package com.andriibryliant.movies.auth.config;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    /**
     * This configuration is active only when the 'dev' profile is active.
     * It allows CORS requests from any origin, which is convenient for local development.
     */
    @Bean
    @Profile("dev")
    public WebMvcConfigurer devCorsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    /**
     * This configuration is active only when the 'prod' profile is active.
     * It is much more restrictive and only allows requests from specific, trusted origins.
     */
    @Bean
    @Profile("prod")
    public WebMvcConfigurer prodCorsConfigurer(@Value("${cors.allowed-origins}") String allowedOrigins) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins.split(",")) // Use configured origins
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
