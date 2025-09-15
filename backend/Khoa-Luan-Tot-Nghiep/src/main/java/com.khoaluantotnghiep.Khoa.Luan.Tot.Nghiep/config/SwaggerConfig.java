package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Khóa Luận Tốt Nghiệp API")
                        .description("API documentation for Recruitment System")
                        .version("1.0.0"));
    }
}
