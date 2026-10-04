package com.santosh.rfq.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that produces an OpenAPI / Swagger definition bean.
 *
 * Golden Rule: Use @Configuration to register 3rd-party library beans into the
 * Spring ApplicationContext.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RFQ to Quote Automation REST API")
                        .version("1.0.0")
                        .description("Production-grade Spring Boot service for managing Requests for Quote (RFQs).")
                        .contact(new Contact()
                                .name("Santosh")
                                .email("santosh@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
