package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedOrigins(
                                "http://localhost:3000",
                                "https://ddc17aa64cfa.ngrok-free.app",
                                "https://khoaluantotnghiep-2026.onrender.com/"
                        )
                        .allowedHeaders("*");
            }
        };
    }
}
