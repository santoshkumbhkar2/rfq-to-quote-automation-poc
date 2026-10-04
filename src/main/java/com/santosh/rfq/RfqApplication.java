package com.santosh.rfq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot application.
 *
 * @SpringBootApplication is a meta-annotation that bundles:
 * 1. @Configuration: Tags the class as a source of bean definitions for the application context.
 * 2. @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings.
 * 3. @ComponentScan: Tells Spring to scan for other components, configurations, and services in this package and sub-packages.
 */
@SpringBootApplication
public class RfqApplication {

    public static void main(String[] args) {
        SpringApplication.run(RfqApplication.class, args);
    }
}
