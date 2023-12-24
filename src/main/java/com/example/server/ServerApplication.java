package com.example.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ServerApplication {

    public static void main(String[] args) {
        // Set headless mode to false
        System.setProperty("java.awt.headless", "false");

        // Run the Spring Boot application
        ConfigurableApplicationContext context = new SpringApplicationBuilder(ServerApplication.class)
                .headless(false)
                .run(args);
    }
}
