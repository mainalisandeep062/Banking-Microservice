package com.banking.acccountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AcccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcccountServiceApplication.class, args);
    }

}
