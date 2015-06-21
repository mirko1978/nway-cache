package org.mirko.cache.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Application entry point by Spring boot
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@EntityScan(basePackages = "org.mirko.cache.example.model")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}