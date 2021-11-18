package com.example.detail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

//@ComponentScan(basePackages = {"com.example.common", "com.example.detail"})
@SpringBootApplication
@EnableEurekaClient
public class DetailApplication {

    public static void main(String[] args) {
        SpringApplication.run(DetailApplication.class, args);
    }

}
