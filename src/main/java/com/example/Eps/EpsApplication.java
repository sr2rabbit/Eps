package com.example.Eps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = "com.example.Eps") // Unificadas todas las anotaciones aquí
public class EpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EpsApplication.class, args);
    }
}
