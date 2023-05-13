package scs.planus.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    GroupedOpenApi PlanusApi() {
        return GroupedOpenApi.builder()
                .group("planus-api")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authorization");

        Info info = new Info()
                .title("Planus 프로젝트 API Document")
                .version("v1.0")
                .description("Planus 프로젝트 API 명세서입니다.");

        Components components = new Components()
                .addSecuritySchemes("Authorization",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer").bearerFormat("JWT")); //JWT 전역 설정을 위해 추가

        return new OpenAPI()
                .addSecurityItem(securityRequirement) //JWT 전역 설정을 위해 추가
                .components(components)
                .info(info);
    }
}
