package com.codeit.monew.global.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Monew API 문서",
                description = "Monew 프로젝트의 Swagger API 문서입니다."
        ),
        servers = {
                @Server(
                        url = "http://localhost:8081",
                        description = "로컬 서버"
                )
        }
)
public class SwaggerConfig {

}
