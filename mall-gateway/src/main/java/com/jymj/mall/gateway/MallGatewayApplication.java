package com.jymj.mall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.jymj.mall"})
public class MallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallGatewayApplication.class, args);
    }

}
