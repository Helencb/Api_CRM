package com.helen.api_crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ApiCrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiCrmApplication.class, args);
    }
}
