package HealthApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.health",
    "com.health.service",
    "com.health.dao",
    "com.health.controller",
    "com.health.config",
    "com.health.model",
    "com.health.Repository",
    "com.health.dto"
})
@EntityScan(basePackages = {
    "com.health.model"
})
@EnableJpaRepositories(basePackages = {
    "com.health.dao",
    "com.health.Repository"
})
public class HealthApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthApplication.class, args);
    }
}