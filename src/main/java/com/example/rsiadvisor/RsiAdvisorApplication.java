package com.example.rsiadvisor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RsiAdvisorApplication {

    public static void main(String[] args) {
        SpringApplication.run(RsiAdvisorApplication.class, args);
    }

}
