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
import org.springframework.http.HttpHeaders;
import scs.planus.infra.swagger.SwaggerOperationCustomizer;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final SwaggerOperationCustomizer swaggerOperationCustomizer;

    @Bean
    GroupedOpenApi PlanusApi() {
        return GroupedOpenApi.builder()
                .group("planus-api")
                .pathsToMatch("/**")
                .addOperationCustomizer(swaggerOperationCustomizer)
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Planus 프로젝트 API Document")
                .version("v1.0")
                .description("Planus 프로젝트 API 명세서입니다.");

        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);

        SecurityRequirement addSecurityItem = new SecurityRequirement();
        addSecurityItem.addList("JWT");

        return new OpenAPI()
                .addSecurityItem(addSecurityItem) //JWT 전역 설정을 위해 추가
                .components(new Components().addSecuritySchemes("JWT", bearerAuth))
                .info(info);
    }
}
