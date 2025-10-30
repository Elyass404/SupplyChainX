package com.supplychainx.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI supplyChainXOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SupplyChainX API Documentation")
                        .description("REST API documentation for the SupplyChainX application.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SupplyChainX Team")
                                .email("support@supplychainx.com")
                                .url("https://github.com/supplychainx"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                );
    }
}
