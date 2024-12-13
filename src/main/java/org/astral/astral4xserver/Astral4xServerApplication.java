package org.astral.astral4xserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class Astral4xServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(Astral4xServerApplication.class, args);
    }
}
