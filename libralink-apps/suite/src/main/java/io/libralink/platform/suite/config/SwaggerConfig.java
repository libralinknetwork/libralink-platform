package io.libralink.platform.suite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket agentApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Agent API")
                .select()
                .paths(PathSelectors.ant("/agent/**"))
                .build();
    }

    @Bean
    public Docket protocolApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Agent Protocol")
                .select()
                .paths(PathSelectors.ant("/protocol/**"))
                .build();
    }

    @Bean
    public Docket internalApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("System API")
                .select()
                .paths(PathSelectors.ant("/internal/**"))
                .build();
    }

    @Bean
    public Docket allApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("All")
            .select()
            .paths(PathSelectors.any())
            .build();
    }
}
