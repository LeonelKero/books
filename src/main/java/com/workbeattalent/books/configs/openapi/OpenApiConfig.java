package com.workbeattalent.books.configs.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiConfigs() {
        return new OpenAPI()
                .info(new Info()
                        .title("Books API")
                        .version("1.0")
                        .description("Api to record red books, no matter the topic")
                        .contact(new Contact().name("leonel ka").email("waboleonel@gmail.com"))
                );
    }

}
