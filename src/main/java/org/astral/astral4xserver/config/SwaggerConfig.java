package org.astral.astral4xserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.astral.astral4xserver.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private springfox.documentation.service.ApiInfo apiInfo() {
        return new springfox.documentation.service.ApiInfo(
                "Astral4xServer API",
                "API documentation for Astral4xServer",
                "1.0",
                "Terms of service",
                new springfox.documentation.service.Contact("AstralStudio", "http://www.godserver.com", "astralpath@163.com"),
                "License of API",
                "https://www.gnu.org/licenses/gpl-3.0.zh-cn.html#license-text",
                Collections.emptyList());
    }
}
