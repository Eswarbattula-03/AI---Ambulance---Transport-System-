package com.example.liferescue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LifeRescueApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifeRescueApplication.class, args);
    }
}
