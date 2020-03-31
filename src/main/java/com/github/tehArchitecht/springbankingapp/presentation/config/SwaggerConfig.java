package com.github.tehArchitecht.springbankingapp.presentation.config;

import com.google.common.collect.Lists;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                // use all methods on all controllers in the presentation layer (excludes basic-error-controller)
                .apis(RequestHandlerSelectors.basePackage("com.github.tehArchitecht.springbankingapp.presentation"))
                .paths(PathSelectors.any())
                .build()
                // use an apiKey scheme for authentication
                .securitySchemes(Lists.newArrayList(apiKey()))
                .securityContexts(Lists.newArrayList(securityContext()))
                // add additional meta information
                .apiInfo(apiInfo());
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                // use the jwt token (apiKey scheme) for all paths
                .securityReferences(Lists.newArrayList(defaultAuth()))
                .forPaths(PathSelectors.any())
                .build();
    }

    private SecurityReference defaultAuth() {
        AuthorizationScope[] scopes = { new AuthorizationScope("global", "accessEverything") };
        return new SecurityReference("Bearer", scopes);
    }

    private ApiKey apiKey() {
        return new ApiKey("Bearer", "Authorization", "header");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SpringBankingApp")
                .description("A toy banking application powered by Spring Framework")
                .build();
    }
}
