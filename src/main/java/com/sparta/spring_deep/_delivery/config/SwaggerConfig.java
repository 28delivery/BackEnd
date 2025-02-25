package com.sparta.spring_deep._delivery.config;

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
                .title("[이팔배달] AI 활용 비즈니스 프로젝트")
                .description("스프링 심화 3기 28조")
                .version("1.0.0")
            );
    }

}
