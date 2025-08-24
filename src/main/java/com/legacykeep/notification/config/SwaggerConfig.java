package com.legacykeep.notification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI notificationServiceOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8083");
        devServer.setDescription("Development server for Notification Service");

        Contact contact = new Contact();
        contact.setEmail("lohithsurisetti@gmail.com");
        contact.setName("LegacyKeep Team");
        contact.setUrl("https://github.com/lohithsurisetti-dev");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("LegacyKeep Notification Service API")
                .version("1.0.0")
                .contact(contact)
                .description("This API provides comprehensive notification services for LegacyKeep, including email delivery, template management, and notification tracking.")
                .termsOfService("https://legacykeep.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
