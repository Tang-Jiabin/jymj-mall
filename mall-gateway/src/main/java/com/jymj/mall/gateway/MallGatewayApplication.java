package com.jymj.mall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableCaching
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.jymj.mall"})
public class MallGatewayApplication {

    public static void main(String[] args) {
        System.setProperty("csp.sentinel.app.type","1");
        SpringApplication.run(MallGatewayApplication.class, args);
    }

}
