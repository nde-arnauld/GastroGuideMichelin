package com.cours_m2.gastroguidemichelin.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Development");

        Contact myContact = new Contact();
        myContact.setName("Arnauld Wilfride Nde Soh");
        myContact.setEmail("ndearnauld2000@gmail.com");

        Info information = new Info()
                .title("GastroGuideMichelin API")
                .version("1.0")
                .description("Cette API expose des endpoints pour gérer des restaurants et leurs évaluations.")
                .contact(myContact);
        return new OpenAPI().info(information).servers(List.of(server));
    }
}
