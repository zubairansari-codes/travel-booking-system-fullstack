package com.zubair.travel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI travelBookingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Travel Booking System API")
                        .description("REST API for Travel Booking System - Book flights, hotels, and manage travel reservations")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Zubair Ansari")
                                .email("support@travelbooking.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
